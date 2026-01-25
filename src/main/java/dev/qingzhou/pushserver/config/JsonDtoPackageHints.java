package dev.qingzhou.pushserver.config;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.util.ClassUtils;

@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(JsonDtoPackageHints.DtoHints.class)
public class JsonDtoPackageHints {

    static class DtoHints implements RuntimeHintsRegistrar {

        // 你要批量注册的 DTO 包（可加多个）
        private static final String[] DTO_PACKAGES = {
                "dev.qingzhou.pushserver.model.dto",
                "dev.qingzhou.pushserver.model.dto.portal"
        };

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // 不使用默认过滤器（默认只扫 @Component 那些）
            ClassPathScanningCandidateComponentProvider scanner =
                    new ClassPathScanningCandidateComponentProvider(false);

            // 放开：所有“独立类”（顶级类/静态内部类）都进来
            TypeFilter includeAllIndependent = (metadataReader, metadataReaderFactory) ->
                    metadataReader.getClassMetadata().isIndependent();

            scanner.addIncludeFilter(includeAllIndependent);

            for (String basePackage : DTO_PACKAGES) {
                for (var bd : scanner.findCandidateComponents(basePackage)) {
                    String className = bd.getBeanClassName();
                    if (className == null) continue;

                    // 过滤一些你不想注册的（可按需调整）
                    if (className.endsWith("package-info")) continue;

                    try {
                        Class<?> clazz = ClassUtils.forName(className, classLoader);

                        // Jackson 绑定常用：构造器/方法/字段
                        hints.reflection().registerType(
                                clazz,
                                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                                MemberCategory.INVOKE_PUBLIC_METHODS
                        );
                    } catch (Throwable ignored) {
                        // 某些类可能因为缺依赖加载失败：这里忽略，避免打包中断
                        // 你也可以改成打印日志帮助定位
                    }
                }
            }
        }
    }
}
