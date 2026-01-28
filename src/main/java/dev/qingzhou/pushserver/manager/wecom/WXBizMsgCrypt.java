package dev.qingzhou.pushserver.manager.wecom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

/**
 * 提供接收和推送给企业微信消息的加解密接口(v1.1). <ol> <li>第三方回复加密消息给企业微信</li> <li>第三方收到企业微信发送的消息，验证签名，解密</li> </ol>
 * 说明：异常java.security.InvalidKeyException:illegal Key Size的解决方案
 * <ol> <li>在官方网站下载JCE无限制权限策略文件（JDK7的下载地址： http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432125.html</li>
 * <li>下载后解压，可以看到local_policy.jar和US_export_policy.jar以及readme.txt</li> 
 * <li>如果安装了JRE，将两个jar文件放到%JRE_HOME%\lib\security目录下覆盖原来的文件</li>
 * <li>如果安装了JDK，将两个jar文件放到%JDK_HOME%\jre\lib\security目录下覆盖原来文件</li> </ol>
 */
public class WXBizMsgCrypt {
    static Charset CHARSET = StandardCharsets.UTF_8;
    Base64.Decoder base64Decoder = Base64.getDecoder();
    Base64.Encoder base64Encoder = Base64.getEncoder();
    byte[] aesKey;
    String token;
    String receiveId;

    /**
     * 构造函数
     *
     * @param token          企业微信后台，开发者设置的token
     * @param encodingAesKey 企业微信后台，开发者设置的EncodingAESKey
     * @param receiveId      不同场景含义不同，appId或者corpId
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public WXBizMsgCrypt(String token, String encodingAesKey, String receiveId) throws AesException {
        if (encodingAesKey.length() != 43) {
            throw new AesException(AesException.IllegalAesKey);
        }
        this.token = token;
        this.receiveId = receiveId;
        aesKey = base64Decoder.decode(encodingAesKey + "=");
    }

    // 生成4个字节的网络字节序
    byte[] getNetworkBytesOrder(int sourceNumber) {
        byte[] orderBytes = new byte[4];
        orderBytes[3] = (byte) (sourceNumber & 0xFF);
        orderBytes[2] = (byte) (sourceNumber >> 8 & 0xFF);
        orderBytes[1] = (byte) (sourceNumber >> 16 & 0xFF);
        orderBytes[0] = (byte) (sourceNumber >> 24 & 0xFF);
        return orderBytes;
    }

    // 还原4个字节的网络字节序
    int recoverNetworkBytesOrder(byte[] orderBytes) {
        int sourceNumber = 0;
        for (int i = 0; i < 4; i++) {
            sourceNumber <<= 8;
            sourceNumber |= orderBytes[i] & 0xff;
        }
        return sourceNumber;
    }

    // 随机生成16位字符串
    String getRandomStr() {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 16; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 对明文进行加密.
     *
     * @param text 需要加密的明文
     * @return 加密后base64编码的字符串
     * @throws AesException aes加密失败
     */
    String encrypt(String randomStr, String text) throws AesException {
        ByteGroup byteCollector = new ByteGroup();
        byte[] randomStrBytes = randomStr.getBytes(CHARSET);
        byte[] textBytes = text.getBytes(CHARSET);
        byte[] networkBytesOrder = getNetworkBytesOrder(textBytes.length);
        byte[] receiveIdBytes = receiveId.getBytes(CHARSET);

        // randomStr + networkBytesOrder + text + receiveId
        byteCollector.addBytes(randomStrBytes);
        byteCollector.addBytes(networkBytesOrder);
        byteCollector.addBytes(textBytes);
        byteCollector.addBytes(receiveIdBytes);

        // ... + pad: 使用自定义的填充方式对明文进行补位填充
        byte[] padBytes = PKCS7Encoder.encode(byteCollector.size());
        byteCollector.addBytes(padBytes);

        // 获得最终的字节流, 未加密
        byte[] unencrypted = byteCollector.toBytes();

        try {
            // 设置加密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

            // 加密
            byte[] encrypted = cipher.doFinal(unencrypted);

            // 使用BASE64对加密后的字符串进行编码
            return base64Encoder.encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.EncryptAESError);
        }
    }

    /**
     * 对密文进行解密.
     *
     * @param text 需要解密的密文
     * @return 解密得到的明文
     * @throws AesException aes解密失败
     */
    String decrypt(String text) throws AesException {
        byte[] original;
        try {
            // 设置解密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec key_spec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
            cipher.init(Cipher.DECRYPT_MODE, key_spec, iv);

            // 使用BASE64对密文进行解码
            byte[] encrypted = base64Decoder.decode(text);

            // 解密
            original = cipher.doFinal(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.DecryptAESError);
        }

        String xmlContent, from_receiveId;
        try {
            // 去除补位字符
            byte[] bytes = PKCS7Encoder.decode(original);

            // 分离16位随机字符串,网络字节序和receiveId
            byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);

            int xmlLength = recoverNetworkBytesOrder(networkOrder);

            xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET);
            from_receiveId = new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length), CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.IllegalBuffer);
        }

        // receiveId不相同的情况
        if (!from_receiveId.equals(receiveId)) {
            throw new AesException(AesException.ValidateCorpidError);
        }
        return xmlContent;

    }

    /**
     * 将公众平台回复用户的消息加密打包.
     * <ol>
     * 	<li>对要发送的消息进行AES-CBC加密</li>
     * 	<li>生成安全签名</li>
     * 	<li>将消息密文和时间戳、随机数字、安全签名打包成xml格式</li>
     * </ol>
     *
     * @param replyMsg  公众平台待回复用户的消息，xml格式的字符串
     * @param timeStamp 时间戳，可以自己生成，也可以用URL参数的timestamp
     * @param nonce     随机串，可以自己生成，也可以用URL参数的nonce
     * @return 加密后的可以直接回复用户的xml
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String EncryptMsg(String replyMsg, String timeStamp, String nonce) throws AesException {
        // 加密
        String encrypt = encrypt(getRandomStr(), replyMsg);

        // 生成安全签名
        if (timeStamp == "") {
            timeStamp = Long.toString(System.currentTimeMillis());
        }

        String signature = SHA1.getSHA1(token, timeStamp, nonce, encrypt);

        // 生成发送的xml
        String result = XMLParse.generate(encrypt, signature, timeStamp, nonce);
        return result;
    }

    /**
     * 验证URL
     *
     * @param msgSignature 签名串，对应URL参数的msg_signature
     * @param timeStamp    时间戳，对应URL参数的timestamp
     * @param nonce        随机串，对应URL参数的nonce
     * @param echoStr      随机串，对应URL参数的echostr
     * @return 解密之后的echostr
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String VerifyURL(String msgSignature, String timeStamp, String nonce, String echoStr) throws AesException {
        String signature = SHA1.getSHA1(token, timeStamp, nonce, echoStr);

        if (!signature.equals(msgSignature)) {
            throw new AesException(AesException.ValidateSignatureError);
        }

        String result = decrypt(echoStr);
        return result;
    }

    /**
     * 检验消息的真实性，并且获取解密后的明文.
     * <ol>
     * 	<li>利用收到的密文生成安全签名，进行签名验证</li>
     * 	<li>若验证通过，则提取xml中的加密消息</li>
     * 	<li>对消息进行解密</li>
     * </ol>
     *
     * @param msgSignature 签名串，对应URL参数的msg_signature
     * @param timeStamp    时间戳，对应URL参数的timestamp
     * @param nonce        随机串，对应URL参数的nonce
     * @param postData     密文，对应POST请求的数据
     * @return 解密后的原文
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String DecryptMsg(String msgSignature, String timeStamp, String nonce, String postData) throws AesException {
        // 提取密文
        Object[] encrypt = XMLParse.extract(postData);

        // 验证安全签名
        String signature = SHA1.getSHA1(token, timeStamp, nonce, encrypt[1].toString());

        // 和URL中的签名比较是否相等
        if (!signature.equals(msgSignature)) {
            throw new AesException(AesException.ValidateSignatureError);
        }

        // 解密
        String result = decrypt(encrypt[1].toString());
        return result;
    }

    // -----------------------------------------------------------
    // 内部类: PKCS7Encoder
    // -----------------------------------------------------------
    static class PKCS7Encoder {
        static Charset CHARSET = StandardCharsets.UTF_8;
        static int BLOCK_SIZE = 32;

        /**
         * 获得对明文进行补位填充的字节.
         *
         * @param count 需要进行填充补位操作的明文长度
         * @return 补齐用的字节数组
         */
        static byte[] encode(int count) {
            // 计算需要填充的位数
            int amountToPad = BLOCK_SIZE - (count % BLOCK_SIZE);
            if (amountToPad == 0) {
                amountToPad = BLOCK_SIZE;
            }
            // 获得补位所用的字符
            char padChr = chr(amountToPad);
            String tmp = new String();
            for (int index = 0; index < amountToPad; index++) {
                tmp += padChr;
            }
            return tmp.getBytes(CHARSET);
        }

        /**
         * 删除解密后明文的补位字符
         *
         * @param decrypted 解密后的明文
         * @return 删除补位字符后的明文
         */
        static byte[] decode(byte[] decrypted) {
            int pad = (int) decrypted[decrypted.length - 1];
            if (pad < 1 || pad > 32) {
                pad = 0;
            }
            return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
        }

        /**
         * 将数字转化成ASCII码对应的字符，用于对明文进行补码
         *
         * @param a 需要转化的数字
         * @return 转化得到的字符
         */
        static char chr(int a) {
            byte target = (byte) (a & 0xFF);
            return (char) target;
        }
    }

    // -----------------------------------------------------------
    // 内部类: ByteGroup
    // -----------------------------------------------------------
    static class ByteGroup {
        java.util.ArrayList<Byte> byteContainer = new java.util.ArrayList<Byte>();

        public byte[] toBytes() {
            byte[] bytes = new byte[byteContainer.size()];
            for (int i = 0; i < byteContainer.size(); i++) {
                bytes[i] = byteContainer.get(i);
            }
            return bytes;
        }

        public ByteGroup addBytes(byte[] bytes) {
            for (byte b : bytes) {
                byteContainer.add(b);
            }
            return this;
        }

        public int size() {
            return byteContainer.size();
        }
    }

    // -----------------------------------------------------------
    // 内部类: SHA1
    // -----------------------------------------------------------
    static class SHA1 {
        /**
         * 用SHA1算法生成安全签名
         *
         * @param token     票据
         * @param timestamp 时间戳
         * @param nonce     随机字符串
         * @param encrypt   密文
         * @return 安全签名
         * @throws AesException
         */
        public static String getSHA1(String token, String timestamp, String nonce, String encrypt) throws AesException {
            try {
                String[] array = new String[]{token, timestamp, nonce, encrypt};
                StringBuffer sb = new StringBuffer();
                // 字符串排序
                Arrays.sort(array);
                for (int i = 0; i < 4; i++) {
                    sb.append(array[i]);
                }
                String str = sb.toString();
                // SHA1签名生成
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(str.getBytes());
                byte[] digest = md.digest();

                StringBuffer hexstr = new StringBuffer();
                String shaHex = "";
                for (int i = 0; i < digest.length; i++) {
                    shaHex = Integer.toHexString(digest[i] & 0xFF);
                    if (shaHex.length() < 2) {
                        hexstr.append(0);
                    }
                    hexstr.append(shaHex);
                }
                return hexstr.toString();
            } catch (Exception e) {
                e.printStackTrace();
                throw new AesException(AesException.ComputeSignatureError);
            }
        }
    }

    // -----------------------------------------------------------
    // 内部类: XMLParse
    // -----------------------------------------------------------
    static class XMLParse {
        /**
         * 提取出xml数据包中的加密消息
         *
         * @param xmltext 待提取的xml字符串
         * @return 提取出的加密消息字符串
         * @throws AesException
         */
        public static Object[] extract(String xmltext) throws AesException {
            Object[] result = new Object[3];
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                
                // XXE 防护
                dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
                dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                dbf.setXIncludeAware(false);
                dbf.setExpandEntityReferences(false);

                DocumentBuilder db = dbf.newDocumentBuilder();
                StringReader sr = new StringReader(xmltext);
                InputSource is = new InputSource(sr);
                Document document = db.parse(is);

                Element root = document.getDocumentElement();
                NodeList nodelist1 = root.getElementsByTagName("Encrypt");
                result[0] = 0;
                result[1] = nodelist1.item(0).getTextContent();
                //result[2] = root.getElementsByTagName("ToUserName").item(0).getTextContent();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                throw new AesException(AesException.ParseXmlError);
            }
        }

        /**
         * 生成xml消息
         *
         * @param encrypt   加密后的消息密文
         * @param signature 安全签名
         * @param timestamp 时间戳
         * @param nonce     随机字符串
         * @return 生成的xml字符串
         */
        public static String generate(String encrypt, String signature, String timestamp, String nonce) {
            String format = "<xml>\n" + "<Encrypt><![CDATA[%1$s]]></Encrypt>\n"
                    + "<MsgSignature><![CDATA[%2$s]]></MsgSignature>\n"
                    + "<TimeStamp>%3$s</TimeStamp>\n" + "<Nonce><![CDATA[%4$s]]></Nonce>\n" + "</xml>";
            return String.format(format, encrypt, signature, timestamp, nonce);
        }
    }
}
