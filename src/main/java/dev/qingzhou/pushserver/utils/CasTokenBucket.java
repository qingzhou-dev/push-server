package dev.qingzhou.pushserver.utils;

import java.util.concurrent.atomic.AtomicReference;

public class CasTokenBucket {

    private final long capacity;          // 最大令牌数（burst）
    private final double refillPerNanos;  // 每纳秒生成的令牌数
    private final AtomicReference<State> state;

    public CasTokenBucket(long capacity, long qps) {
        if (capacity <= 0 || qps <= 0) throw new IllegalArgumentException("capacity/qps must be > 0");
        this.capacity = capacity;
        this.refillPerNanos = qps / 1_000_000_000.0;
        long now = System.nanoTime();
        this.state = new AtomicReference<>(new State(capacity, now));
    }

    private record State(double tokens, long lastRefillNanos) {}

    public boolean tryAcquire() {
        int spins = 0;
        while (true) {
            State cur = state.get();
            long now = System.nanoTime();

            long delta = now - cur.lastRefillNanos;
            if (delta < 0) delta = 0; // 理论上 nanoTime 单调，但防御性写一下

            double newTokens = cur.tokens;
            if (delta > 0) {
                double generated = delta * refillPerNanos;
                newTokens = Math.min(capacity, newTokens + generated);
            }

            // 不管成功/失败，都尽量把时间推进，减少下次重复计算
            if (newTokens < 1.0) {
                State next = (delta > 0) ? new State(newTokens, now) : cur;
                if (next == cur || state.compareAndSet(cur, next)) {
                    return false;
                }
            } else {
                State next = new State(newTokens - 1.0, now);
                if (state.compareAndSet(cur, next)) {
                    return true;
                }
            }

            // 轻微退避，避免高竞争 CPU 飙
            if (++spins > 10) {
                Thread.onSpinWait();
            }
        }
    }
}
