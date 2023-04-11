package com.walleto;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WalletBankingApplication  {


	public static void main(String[] args) {
		SpringApplication.run(WalletBankingApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}
//	public Object[]  demo(){
//		String demo1[]= new String[]{"nextuple"};
//		String demo2[]= new String[]{"INC"};
//
//		Object result [] = new Object[]{demo1,demo2};
//		return result;
//	}
//	public void print(){
//		Object result []= demo();
//		System.out.println("print");
//		for(Object i : result){
//			String re [] = (String[]) i;
//			for(String j:re){
//				System.out.println(j);
//			}
//		}
//	}
}

