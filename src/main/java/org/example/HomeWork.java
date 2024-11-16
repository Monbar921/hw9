package org.example;


import lombok.SneakyThrows;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HomeWork {
    private BufferedReader reader;
    private Treap treap;

    public HomeWork(){
        treap = new Treap();
    }
    /**
     * <h1>Задание 1.</h1>
     * Решить задачу UPIT из файла contest7_tasks.pdf
     */
    @SneakyThrows
    public void upit(InputStream in, OutputStream out) {

        int lineNumber = 0;

        while(true){
            String line = getNextLine(in);
            if(line == null){
                break;
            }

            Operation operation = parseLine(line, lineNumber);
            handleOperation(operation);

            ++lineNumber;
        }
    }

    private String getNextLine(InputStream in){
        String line;

        if(reader == null){
            reader = new BufferedReader(new InputStreamReader(in));
        }
        try {
            line = reader.readLine();
            if(line == null){
                reader.close();
            }
        } catch (IOException e) {
            line = null;
            try {
                reader.close();
            } catch (IOException ignore) {

            }
        }

        return line;
    }

    private Operation parseLine(String line, int lineNumber){
        Operation operation = null;

        String[] elements = line.split(" ");
        int seqLength = 0;
        int queries = 0;

        if(elements.length > 0){
            try {
                if (lineNumber == 0) {
                    seqLength = Integer.parseInt(elements[0]);
                    queries = Integer.parseInt(elements[1]);
                } else if (lineNumber == 1) {
                    for (String el : elements) {
                        treap.add(Long.valueOf(el));
                    }
                } else {
                    List<Long> operands = new ArrayList<>();

                    for (int i = 1; i < elements.length; ++i){
                        operands.add(Long.parseLong(elements[i]));
                    }

                    operation = new Operation(Integer.parseInt(elements[0]), operands);
                }
            }catch (NumberFormatException ignore){

            }
        }

        return operation;
    }

    private void handleOperation(Operation operation){
        if(operation != null){
            if(operation.type == 1){
                treap.addToAll(operation.operands.get(0).intValue() - 1, operation.operands.get(1).intValue() - 1
                        , operation.operands.get(2));
            }else if(operation.type == 2){

            }else if(operation.type == 3){

            }else if(operation.type == 4){

            }
        }
    }

    private static class Operation{
        int type;
        List<Long> operands;

        public Operation(int type, List<Long> operands) {
            this.type = type;
            this.operands = operands;
        }
    }

}
