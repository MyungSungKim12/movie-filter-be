package com.project.moviefilterbe.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ImageUtil {
    private final AmazonS3 s3Client;
    private static String bucketName;
    private static String publicUrl;

    @Value("${application.bucket.name}")
    public void setBucketName(String value) {
        bucketName = value;
    }
    @Value("${cloud.cloudflare.r2.public-url:}")
    public void setPublicUrl(String value) {
        publicUrl = value;
    }

    public Map<String, String> imageUploadS3(MultipartFile file) {
        Map<String, String> result = new HashMap<>();

        try {
            String imageFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            String fullPath = "profile/" + imageFileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            s3Client.putObject(new PutObjectRequest(bucketName, fullPath, file.getInputStream(), metadata));

            String urlText;
            if (!publicUrl.isEmpty()) {
                urlText = publicUrl + "/" + fullPath;
            } else {
                urlText = s3Client.getUrl(bucketName, fullPath).toString();
            }

            result.put("imgName", imageFileName);
            result.put("imgUrl", urlText);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void ImageDeleteS3(String beforeImage) {
        try {
            String fullPath = "profile/" + beforeImage;
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, fullPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
