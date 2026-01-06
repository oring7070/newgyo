package kr.co.newgyo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 메시지 컨버터 설정 (JSON 처리)
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        // 타임아웃 설정 (선택사항)
//        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
//        factory.setConnectTimeout(5000);  // 5초
//        factory.setReadTimeout(10000);    // 10초
//        restTemplate.setRequestFactory(factory);

        return restTemplate;
    }
}