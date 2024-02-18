package com.searcher;


public class HelloWorld {
    public static void main(String[] args) throws Exception {

        PDFManager manager = new PDFManager();
        


        manager.openDocument("src/main/resources/sample_pdf/CP Handbook_2023-24_230915.pdf");
        
        
        //manager.searchLexical(0, new String[] {"p", "lan", "g"}, new String[] {"OR","AND","NOT"}, "s");
        //manager.searchKeyword(0, null, "PRON", false);
        //manager.searchSemantic(0, "generative AI", 40);

        ControlUtil.stopResultQueueThread();
        ControlUtil.stopSearchEngineThread();
        
    }
}
