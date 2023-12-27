package com.searcher;

public class HelloWorld {
    
    public static void main(String[] args) throws Exception {
        
        ControlUtil.startSearchEngineThread();
        Thread.sleep(1000);

        //String filepath = "src/main/resources/sample_pdf/CP Handbook_2023-24_230915.pdf"; 
        //PDFDocument doc = new PDFDocument(filepath);
        //doc.sendTextToServer();

        Thread.sleep(2000);
        ControlUtil.stopSearchEngineThread();
    }
}
