package com.example.test.flux;

import reactor.core.publisher.Flux;

/**
 * 
 * StaticLoggerBinder
 * 
 * @author lsy
 *
 */
public class FluxT {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Flux.just("a", "b","c")
        .log()
        .compose(stringFlux -> {
            System.out.println("compose executed.....................");
            return stringFlux.map(e -> e + "#");
        }).subscribe(System.out::println);
		
		String temp2="";
		Flux.just("1","2","3")
        .log()
        .flatMap(s -> {
        	System.out.println("flatMap executed............");
//        	temp2+=s;
            return Flux.just(s+"$");
        }).subscribe(System.out::println);
		
		
		
	}

}
