package cz.vsb.vea.cz.vsb.vea.lab02v2;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cz.vsb.vea.cz.vsb.vea.lab02v2.converters.LocalDateConverter;

@Configuration
public class ConvertersConfig implements WebMvcConfigurer{

	@Override
	public void addFormatters(FormatterRegistry registry) {
		WebMvcConfigurer.super.addFormatters(registry);
		registry.addConverter(new LocalDateConverter());
	}

	
	
	
}
