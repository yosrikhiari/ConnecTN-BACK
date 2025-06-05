package tn.esprit.spring.connectn.Config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "derhpiiee",
                "api_key", "825528185195791",
                "api_secret", "M66t8xr4Zl7qRosb1XbYPcEWc6o"
        ));
    }
}
