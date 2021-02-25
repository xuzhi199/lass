package com.aorise.file.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.auth.COSSigner;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.tencent.cloud.CosStsClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.Date;
import java.util.TreeMap;

public class CredentialUtil {

    @Value("${file.tencent.domain}")
    private String domain;
    @Value("${file.tencent.accessKeyId}")
    private String accessKeyId;
    @Value("${file.tencent.accessKeySecret}")
    private String accessKeySecret;
    @Value("${file.tencent.bucketName}")
    private String bucketName;
    @Value("${file.tencent.region}")
    private String region;
    /**
     * 获取临时凭证
     *
     * @param
     * @return
     */
    public JSONObject getTemporaryCertificate(){
        TreeMap<String, Object> config = new TreeMap<String, Object>();
        try {
            String accessKeyId = "AKIDd0vZy9NuHNCyIWwZLvEIemeqBGPvbFen";
            String accessKeySecret = "2h6F8kHbfSAettcIlxslOkslbDYcdOs7";
            String bucketName = "cloud-service-1258562762";
            String region = "ap-chengdu";
            // 替换为您的 SecretId
            config.put("SecretId", accessKeyId);
            // 替换为您的 SecretKey
            config.put("SecretKey", accessKeySecret);
            // 临时密钥有效时长，单位是秒，默认1800秒，最长可设定有效期为7200秒
            config.put("durationSeconds", 1800);
            // 换成您的 bucket
            config.put("bucket", bucketName);
            // 换成 bucket 所在地区
            config.put("region", region);
            // 这里改成允许的路径前缀，可以根据自己网站的用户登录态判断允许上传的具体路径，例子：a.jpg 或者 a/* 或者 * 。
            // 如果填写了“*”，将允许用户访问所有资源；除非业务需要，否则请按照最小权限原则授予用户相应的访问权限范围。
            config.put("allowPrefix", "*");
            // 密钥的权限列表。简单上传、表单上传和分片上传需要以下的权限，其他权限列表请看 https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[] {
                    // 简单上传
                    "name/cos:PutObject",
                    // 表单上传、小程序上传
                    "name/cos:PostObject",
                    // 分片上传
                    "name/cos:InitiateMultipartUpload",
                    "name/cos:ListMultipartUploads",
                    "name/cos:ListParts",
                    "name/cos:UploadPart",
                    "name/cos:CompleteMultipartUpload"
            };
            config.put("allowActions", allowActions);
            JSONObject credential = CosStsClient.getCredential(config);
            //成功返回临时密钥信息，如下打印密钥信息
//            System.out.println(credential);
            return credential;
        } catch (Exception e) {
            //失败抛出异常
            throw new IllegalArgumentException("no valid secret !");
        }

    }

    public void uploadFile(){
        String region = "ap-chengdu";
        JSONObject jsonObject = (JSONObject) getTemporaryCertificate().get("credentials");
        // 用户基本信息
        String tmpSecretId = jsonObject.get("tmpSecretId").toString();   // 替换为您的 SecretId
        String tmpSecretKey = jsonObject.get("tmpSecretKey").toString();  // 替换为您的 SecretKey
        String sessionToken = jsonObject.get("sessionToken").toString();  // 替换为您的 Token

        // 1 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(tmpSecretId, tmpSecretKey);
        // 2 设置 bucket 区域,详情请参阅 COS 地域 https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        // 3 生成 cos 客户端
        COSClient cosclient = new COSClient(cred, clientConfig);
        // bucket 名需包含 appid
        String bucketName = "cloud-service-1258562762";

        String key = "doc/picture2.jpg";
        // 上传 object, 建议 20M 以下的文件使用该接口
        File localFile = new File("F:\\BaiduNetdiskDownload\\01传智健康1.png");
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);

        // 设置 x-cos-security-token header 字段
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setSecurityToken(sessionToken);
        putObjectRequest.setMetadata(objectMetadata);

        try {
            PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
            // 成功：putobjectResult 会返回文件的 etag
            String etag = putObjectResult.getETag();
        } catch (CosServiceException e) {
            //失败，抛出 CosServiceException
            e.printStackTrace();
        } catch (CosClientException e) {
            //失败，抛出 CosClientException
            e.printStackTrace();
        }
        // 关闭客户端
        cosclient.shutdown();
    }

    public void downloadFile(){
        String secretId = "AKIDd0vZy9NuHNCyIWwZLvEIemeqBGPvbFen";
        String secretKey = "2h6F8kHbfSAettcIlxslOkslbDYcdOs7";
        String bucketName = "cloud-service-1258562762";
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        COSSigner signer = new COSSigner();
        // 设置过期时间为1个小时
        Date expiredTime = new Date(System.currentTimeMillis() + 3600L * 1000L);
        // 要签名的 key, 生成的签名只能用于对应此 key 的下载
        String key = "doc/picture2.jpg";
        String sign = signer.buildAuthorizationStr(HttpMethodName.GET, key, cred, expiredTime);
        System.out.println(sign);
    }

    public static void main(String[] args) {
//        new CredentialUtil().uploadFile();
        new CredentialUtil().downloadFile();
    }
}
