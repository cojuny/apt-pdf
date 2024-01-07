package com.searcher;

public class HelloWorld {
    
    public static void main(String[] args) throws Exception {
        
        ControlUtil.startSearchEngineThread();
        Thread.sleep(5000);
        
        PDFManager manager = new PDFManager();
        manager.openDocument("src/main/resources/sample_pdf/CP Handbook_2023-24_230915.pdf");
        //manager.openDocument("src/main/resources/sample_pdf/Human Res Mgmt Journal - 2015 - De Gieter - How reward satisfaction affects employees  turnover intentions and performance.pdf");
        //manager.openDocument("src/main/resources/sample_pdf/Computer Networking_ A Top-Down Approach, Global Edition, 8th Edition.pdf");
        
        
        //System.out.println(manager.searchLexical(0, new String[] {"Each", "Information"}, new String[] {"NULL", "AND"}, "WORD"));
        manager.searchSemantic(0, "generative AI", 60);
        manager.searchSemantic(0, "generative AI", 60);
        Thread.sleep(5000);

        ControlUtil.stopSearchEngineThread();
    }
}
