package cz.vsb.vea.cz.vsb.vea.lab02v2.converters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;

public class LocalDateConverter implements Converter<String, LocalDate> {

	@Override
	public LocalDate convert(String source) {
		return LocalDate.parse(source, DateTimeFormatter.ofPattern("d. M. uuuu"));
	}

}
