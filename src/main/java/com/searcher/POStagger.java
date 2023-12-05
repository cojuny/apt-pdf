package com.searcher;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;

import java.io.IOException;
import java.io.InputStream;


public class POStagger {

    private String text_raw;
    private String[] text;
    private String[] pos_tags;

    public POStagger(String text_raw) {
        this.text_raw = text_raw;
        this.text = make_token(this.text_raw);
        this.pos_tags = make_pos_tags(this.text);
    }

    private String[] make_token(String input) {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        return tokenizer.tokenize(input);
    }

    private String[] make_pos_tags(String[] input) {

        try {
            // Load the POS model
            InputStream modelIn = getClass().getResourceAsStream("/en-pos-maxent.bin");
            POSModel posModel = new POSModel(modelIn);

            // Initialize the POS tagger
            POSTaggerME posTagger = new POSTaggerME(posModel);

            // Perform POS tagging on the text
            pos_tags = posTagger.tag(input);

            // Clean up resources
            modelIn.close();

            return pos_tags;
        } catch (IOException e) {
            e.printStackTrace();
            return new String[0];
        }

    }

    public String[] getText() {
        return text.clone(); // Return a copy to ensure immutability
    }

    public String[] getPosTags() {
        return pos_tags.clone(); // Return a copy to ensure immutability
    }

     public static void main(String[] args) {
        // Example usage:
        String inputText = "CEO REPORT\n" + //
                "The Psychology of the Unknown:\n" + //
                "What Great Problem-Solvers do\n" + //
                "When They Get StuckTable of Contents\n" + //
                "Execu ve summary1\n" + //
                "Purpose and scope of study2\n" + //
                "Sample breakdown and preliminary ﬁndings2\n" + //
                "What’s keeping execu ves awake at night?3\n" + //
                "Execu ves’ top three advice on what must be done to open up the\n" + //
                "alterna ves3\n" + //
                "Results from clinical psychology interviews4\n" + //
                "Table and map of CEOs’ percep ons when stuck5\n";

        POStagger tagger = new POStagger(inputText);

        String[] text = tagger.getText();
        String[] posTags = tagger.getPosTags();

        for (int i=0; i<text.length; i++) {
            System.out.println(text[i] + "  " + posTags[i]);
        }
    }


}