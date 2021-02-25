package org.example;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }

    public List<Integer> antiNegativeSort(List<Integer> list) {
        List<Integer> output = list.stream().filter(i -> i >= 0).collect(Collectors.toList());
        Collections.sort(output);

        return output;
    }
}

