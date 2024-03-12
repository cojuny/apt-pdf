/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.searcher;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author junyoung
 */
public class UserInterface extends javax.swing.JFrame implements PropertyChangeListener {

    private PDFManager pdfManager = new PDFManager();
    private boolean searchBoxButtonFlag = true;
    private boolean NavigationBoxButtonFlag = true;
    private boolean ResultBoxButtonFlag = true;
    private boolean lock = false;
    private int curView = -1;
    private int numFileSelected = 0;
    private int resultCount = 0; 
    List<Boolean> selectedDocuments = new ArrayList<>(Arrays.asList(false, false, false, false, false));
    DefaultListModel<String> documentListModel = new DefaultListModel<>();
    DefaultListModel<Result> resultListModel = new DefaultListModel<>();
    JTextArea textArea;
    Highlighter highlighter;
    HighlightPainter pinkHighlighter = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
    HighlightPainter yellowHighlighter = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);
    private int[] currentHighlightIndex;
    javax.swing.JList<Result> resultList = new JList<>(resultListModel);
    

    
    /**
     * Creates new form SearchPanelInterface
     */
    public UserInterface() throws Exception {
        initComponents();
        initResultBoxComponent();
        pdfManager.resultHandler.addPropertyChangeListener(this);
    }

    private void initResultBoxComponent() {
        resultList.addMouseListener(new MouseAdapter() {
            @SuppressWarnings("unchecked")
            public void mouseClicked(MouseEvent evt) {
                javax.swing.JList<Result> list = (JList<Result>) evt.getSource();
                if(list == null) {return; }
                if (evt.getClickCount() == 1) {
                    int index = list.locationToIndex(evt.getPoint());
                    if (index < 0) {return; }

                    if (currentHighlightIndex != null) {
                        removeHighlight(currentHighlightIndex[0], currentHighlightIndex[1]);
                        highlight(currentHighlightIndex[0], currentHighlightIndex[1], false);
                    }

                    Result result = pdfManager.resultHandler.fullResults.get(index);
                    int viewIndex = PDFManager.idToIndex(result.getId());
                    if (viewIndex != curView) {
                        switchView(viewIndex);
                    }
                    
                    int[] highlightIndex = {result.getStartIndex(), result.getEndIndex()};
                    removeHighlight(highlightIndex[0], highlightIndex[1]);
                    highlight(highlightIndex[0], highlightIndex[1], true);
                    scrollToTextIndex(highlightIndex[0]);
                    currentHighlightIndex = highlightIndex;
                } 
            }
        });
        ResultListScrollPane.setViewportView(resultList);
    }



    // if lock change detected
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("lock".equals(evt.getPropertyName())) {
            lock = (Boolean) evt.getNewValue();
            if (lock) {
                lock();
            } else {
                unlock();
            }
        } else if ("newResult".equals(evt.getPropertyName())) {
            updateResult();
        }
    }

    private void switchView(int index) {
        curView = index;
        PDFDocument document = PDFManager.documents.get(index);
        String title = "current view: " + document.getTitle();
        if (title.length() > 90) {
            title = title.substring(0, 90);
            title = title + "...";
        }
        ViewLabel.setText(title);
        textArea = new JTextArea();
        textArea.setText(document.getText());
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        highlighter = textArea.getHighlighter();
        if (document.results.size() > 0) {
            for (Result result : document.results) {
                highlight(result.getStartIndex(), result.getEndIndex(), false);
            }
        }
        TextPanel.setViewportView(textArea);
        TextPanel.revalidate(); // Use revalidate() to ensure the layout manager updates
        TextPanel.repaint();
        
    }

    private void updateResult() {
        for (; resultCount < pdfManager.resultHandler.fullResults.size(); resultCount++) {
            Result result = pdfManager.resultHandler.fullResults.get(resultCount);
            int index = PDFManager.idToIndex(result.getId());
            result.processDisplayText(PDFManager.documents.get(index).getText());
            resultListModel.addElement(result);
            highlight(result.getStartIndex(), result.getEndIndex(), false);
        }
    }

    private void highlight(int start, int end, boolean emphasize) {
        if (highlighter == null) {
            return;
        }
        try {
            if (emphasize) {
                highlighter.addHighlight(start, end, pinkHighlighter);
            } else {
                highlighter.addHighlight(start, end, yellowHighlighter);
            }
            
        } catch (Exception e) {
            System.err.println("highlight error!");
        }
    }

    private void removeHighlight(int start, int end) {
        Highlighter.Highlight[] highlights = highlighter.getHighlights();
        for (Highlighter.Highlight highlight : highlights) {
            if (start == highlight.getStartOffset() && end == highlight.getEndOffset()) {
                highlighter.removeHighlight(highlight); 
                break; 
            }
        }
    }

    public void scrollToTextIndex(int index) {
        if (textArea == null) {
            return;
        }
        SwingUtilities.invokeLater( () -> {
            try {
                Rectangle rect = textArea.modelToView(index);
                if (rect != null) {
                    textArea.scrollRectToVisible(rect);
                }
            } catch (BadLocationException e) {
                e.printStackTrace(); 
            }
        }
        );
        
    }

    private void clearResults() {
        resultCount = 0;
        resultListModel.clear();        
        for (PDFDocument document : PDFManager.documents) {
            document.results.clear();
        }
        pdfManager.resultHandler.fullResults.clear();
        if (highlighter != null) {
            highlighter.removeAllHighlights();
        }
    }

    private void openDocument(String filepath) {
        pdfManager.openDocument(filepath);
    }

    private void lock() {
        lock = true;
        pdfManager.resultHandler.lockSearch();
        SearchButton.setEnabled(false);
        NavigationOpenButton.setEnabled(false);
        SearchStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/duck_resized.gif")));
    }

    private void unlock() {
        lock = false;
        if (isFileSelected()) {
            SearchButton.setEnabled(true);
        }
        NavigationOpenButton.setEnabled(true);
        SearchStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/check_resized.png")));
    }

    private void updateSearchButton() {
        SearchButton.setEnabled(isFileSelected());
    }

    private boolean isFileSelected() {
        return numFileSelected > 0;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        MainPanel = new javax.swing.JPanel();
        PDFViewPanel = new javax.swing.JPanel();
        SearchBoxActivationButton = new javax.swing.JButton();
        ResultBoxActivationButton = new javax.swing.JButton();
        NavigationBoxActivationButton = new javax.swing.JButton();
        SearchBoxLayer = new javax.swing.JLayeredPane();
        LexicalSelectorButton = new javax.swing.JButton();
        SemanticSelectorButton = new javax.swing.JButton();
        KeywordSelectorButton = new javax.swing.JButton();
        TextSearchOptions = new javax.swing.JLabel();
        SearchTabPanel = new javax.swing.JTabbedPane();
        Lexical = new javax.swing.JPanel();
        LexicalTitle = new javax.swing.JLabel();
        LexicalSearchInput = new javax.swing.JPanel();
        LexicalText1 = new javax.swing.JTextField();
        LexicalText2 = new javax.swing.JTextField();
        LexicalText3 = new javax.swing.JTextField();
        LexicalText4 = new javax.swing.JTextField();
        LexicalText5 = new javax.swing.JTextField();
        LexicalConnector2 = new javax.swing.JComboBox<>();
        LexicalConnector3 = new javax.swing.JComboBox<>();
        LexicalConnector4 = new javax.swing.JComboBox<>();
        LexicalConnector5 = new javax.swing.JComboBox<>();
        LexicalSearchOptions = new javax.swing.JPanel();
        LexicalRange = new javax.swing.JComboBox<>();
        LexicalRangeLabel = new javax.swing.JLabel();
        Keyword = new javax.swing.JPanel();
        KeywordTitle = new javax.swing.JLabel();
        KeywordSearchInput = new javax.swing.JPanel();
        KeywordText = new javax.swing.JTextField();
        KeywordTextLabel = new javax.swing.JLabel();
        KeywowrdPOSLabel = new javax.swing.JLabel();
        KeywordPOS = new javax.swing.JComboBox<>();
        KeywordSearchOptions = new javax.swing.JPanel();
        KeywordSynonyms = new javax.swing.JCheckBox();
        Semantic = new javax.swing.JPanel();
        SemanticTitle = new javax.swing.JLabel();
        SemanticSearchInput = new javax.swing.JPanel();
        SemanticText = new javax.swing.JTextField();
        SemanticTextLabel = new javax.swing.JLabel();
        SemanticThresholdLabel = new javax.swing.JLabel();
        SemanticThreshold = new javax.swing.JSlider();
        SearchInfoPanel = new javax.swing.JPanel();
        SearchStatus = new javax.swing.JLabel();
        SearchButton = new javax.swing.JButton();
        ViewLabel = new javax.swing.JLabel();
        TextPanel = new javax.swing.JScrollPane();
        ResultBoxLayer = new javax.swing.JLayeredPane();
        ResultBoxMainPanel = new javax.swing.JPanel();
        ResultBoxLabel = new javax.swing.JLabel();
        ResultListScrollPane = new javax.swing.JScrollPane();
        NavigationBoxLayer = new javax.swing.JLayeredPane();
        NavigationMainPanel = new javax.swing.JPanel();
        NavigationLabel = new javax.swing.JLabel();
        NavigationOpenButton = new javax.swing.JButton();
        OpenedDocumentListScrollPane = new javax.swing.JScrollPane();
        OpenedDocumentList = new javax.swing.JList<>(documentListModel);
        NavigationSelectButton = new javax.swing.JButton();
        NavigationViewButton = new javax.swing.JButton();
        NavigationDeleteButton = new javax.swing.JButton();
        NavigationIconPanel = new javax.swing.JPanel();
        Icon0 = new javax.swing.JLabel();
        Icon1 = new javax.swing.JLabel();
        Icon2 = new javax.swing.JLabel();
        Icon3 = new javax.swing.JLabel();
        Icon4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        MainPanel.setBackground(new java.awt.Color(255, 255, 255));
        MainPanel.setPreferredSize(new java.awt.Dimension(1600, 900));

        PDFViewPanel.setBackground(new java.awt.Color(204, 204, 255));
        PDFViewPanel.setMinimumSize(new java.awt.Dimension(1040, 603));
        PDFViewPanel.setLayout(new java.awt.GridBagLayout());

        SearchBoxActivationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/up_arrow_resized.png"))); // NOI18N
        SearchBoxActivationButton.setBorderPainted(false);
        SearchBoxActivationButton.setContentAreaFilled(false);
        SearchBoxActivationButton.setFocusPainted(false);
        SearchBoxActivationButton.setMaximumSize(new java.awt.Dimension(100, 100));
        SearchBoxActivationButton.setMinimumSize(new java.awt.Dimension(70, 40));
        SearchBoxActivationButton.setPreferredSize(new java.awt.Dimension(70, 40));
        SearchBoxActivationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchBoxActivationButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        PDFViewPanel.add(SearchBoxActivationButton, gridBagConstraints);

        ResultBoxActivationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/right_arrow_resized.png"))); // NOI18N
        ResultBoxActivationButton.setContentAreaFilled(false);
        ResultBoxActivationButton.setMinimumSize(new java.awt.Dimension(40, 70));
        ResultBoxActivationButton.setPreferredSize(new java.awt.Dimension(40, 70));
        ResultBoxActivationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResultBoxActivationButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(305, 6, 0, 17);
        PDFViewPanel.add(ResultBoxActivationButton, gridBagConstraints);

        NavigationBoxActivationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/left_arrow_resized.png"))); // NOI18N
        NavigationBoxActivationButton.setContentAreaFilled(false);
        NavigationBoxActivationButton.setMaximumSize(new java.awt.Dimension(40, 70));
        NavigationBoxActivationButton.setMinimumSize(new java.awt.Dimension(40, 70));
        NavigationBoxActivationButton.setPreferredSize(new java.awt.Dimension(40, 70));
        NavigationBoxActivationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NavigationBoxActivationButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(305, 6, 0, 0);
        PDFViewPanel.add(NavigationBoxActivationButton, gridBagConstraints);

        SearchBoxLayer.setBackground(new java.awt.Color(204, 204, 204));
        SearchBoxLayer.setToolTipText("");
        SearchBoxLayer.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        SearchBoxLayer.setDoubleBuffered(true);
        SearchBoxLayer.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        LexicalSelectorButton.setText("Lexical Search");
        LexicalSelectorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LexicalSelectorButtonActionPerformed(evt);
            }
        });
        SearchBoxLayer.add(LexicalSelectorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, 200, 50));

        SemanticSelectorButton.setText("Semantic Search");
        SemanticSelectorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SemanticSelectorButtonActionPerformed(evt);
            }
        });
        SearchBoxLayer.add(SemanticSelectorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 200, 200, 50));

        KeywordSelectorButton.setText("Keyword Search");
        KeywordSelectorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KeywordSelectorButtonActionPerformed(evt);
            }
        });
        SearchBoxLayer.add(KeywordSelectorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 130, 200, 50));

        TextSearchOptions.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TextSearchOptions.setText("Search Options");
        TextSearchOptions.setFocusable(false);
        TextSearchOptions.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        SearchBoxLayer.add(TextSearchOptions, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 260, 39));

        LexicalTitle.setText("Lexical Search");
        LexicalTitle.setMaximumSize(new java.awt.Dimension(170, 20));
        LexicalTitle.setMinimumSize(new java.awt.Dimension(170, 20));

        LexicalConnector2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "OR", "AND", "NOT" }));

        LexicalConnector3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "OR", "AND", "NOT" }));

        LexicalConnector4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "OR", "AND", "NOT" }));

        LexicalConnector5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "OR", "AND", "NOT" }));

        javax.swing.GroupLayout LexicalSearchInputLayout = new javax.swing.GroupLayout(LexicalSearchInput);
        LexicalSearchInput.setLayout(LexicalSearchInputLayout);
        LexicalSearchInputLayout.setHorizontalGroup(
            LexicalSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LexicalSearchInputLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(LexicalSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LexicalConnector2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LexicalConnector3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LexicalConnector4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LexicalConnector5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(LexicalSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(LexicalText5, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                    .addComponent(LexicalText4, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                    .addComponent(LexicalText2, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                    .addComponent(LexicalText1)
                    .addComponent(LexicalText3))
                .addGap(30, 30, 30))
        );
        LexicalSearchInputLayout.setVerticalGroup(
            LexicalSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LexicalSearchInputLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(LexicalText1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LexicalSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LexicalText2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LexicalConnector2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LexicalSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LexicalText3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LexicalConnector3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LexicalSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LexicalText4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LexicalConnector4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LexicalSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LexicalText5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LexicalConnector5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        LexicalRange.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "exact", "word", "sentence" }));

        LexicalRangeLabel.setText("Search Range:");

        javax.swing.GroupLayout LexicalSearchOptionsLayout = new javax.swing.GroupLayout(LexicalSearchOptions);
        LexicalSearchOptions.setLayout(LexicalSearchOptionsLayout);
        LexicalSearchOptionsLayout.setHorizontalGroup(
            LexicalSearchOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LexicalSearchOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(LexicalSearchOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LexicalRange, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LexicalRangeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE))
                .addContainerGap())
        );
        LexicalSearchOptionsLayout.setVerticalGroup(
            LexicalSearchOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LexicalSearchOptionsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LexicalRangeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LexicalRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout LexicalLayout = new javax.swing.GroupLayout(Lexical);
        Lexical.setLayout(LexicalLayout);
        LexicalLayout.setHorizontalGroup(
            LexicalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LexicalLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(LexicalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LexicalTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(LexicalLayout.createSequentialGroup()
                        .addComponent(LexicalSearchInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LexicalSearchOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        LexicalLayout.setVerticalGroup(
            LexicalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LexicalLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(LexicalTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LexicalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(LexicalSearchOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LexicalSearchInput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        SearchTabPanel.addTab("Lexical", Lexical);

        KeywordTitle.setText("Keyword Search");

        KeywordTextLabel.setText("Keyword Input: (word only)");

        KeywowrdPOSLabel.setText("Part-of-Speech Tag:");

        KeywordPOS.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NONE", "ADJ", "ADP", "ADV", "CONJ", "DET", "NOUN", "NUM", "PRT", "PRON", "VERB", "." }));

        javax.swing.GroupLayout KeywordSearchInputLayout = new javax.swing.GroupLayout(KeywordSearchInput);
        KeywordSearchInput.setLayout(KeywordSearchInputLayout);
        KeywordSearchInputLayout.setHorizontalGroup(
            KeywordSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, KeywordSearchInputLayout.createSequentialGroup()
                .addGap(0, 112, Short.MAX_VALUE)
                .addComponent(KeywordText, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
            .addGroup(KeywordSearchInputLayout.createSequentialGroup()
                .addGroup(KeywordSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(KeywordSearchInputLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(KeywordSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(KeywowrdPOSLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(KeywordTextLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(KeywordSearchInputLayout.createSequentialGroup()
                        .addGap(113, 113, 113)
                        .addComponent(KeywordPOS, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        KeywordSearchInputLayout.setVerticalGroup(
            KeywordSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(KeywordSearchInputLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(KeywordTextLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(KeywordText, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(KeywowrdPOSLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(KeywordPOS, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        KeywordSynonyms.setText("Include Synonyms");

        javax.swing.GroupLayout KeywordSearchOptionsLayout = new javax.swing.GroupLayout(KeywordSearchOptions);
        KeywordSearchOptions.setLayout(KeywordSearchOptionsLayout);
        KeywordSearchOptionsLayout.setHorizontalGroup(
            KeywordSearchOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(KeywordSynonyms)
        );
        KeywordSearchOptionsLayout.setVerticalGroup(
            KeywordSearchOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, KeywordSearchOptionsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(KeywordSynonyms)
                .addContainerGap())
        );

        javax.swing.GroupLayout KeywordLayout = new javax.swing.GroupLayout(Keyword);
        Keyword.setLayout(KeywordLayout);
        KeywordLayout.setHorizontalGroup(
            KeywordLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(KeywordLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(KeywordLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(KeywordLayout.createSequentialGroup()
                        .addComponent(KeywordSearchInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(KeywordSearchOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(KeywordTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
        );
        KeywordLayout.setVerticalGroup(
            KeywordLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, KeywordLayout.createSequentialGroup()
                .addGroup(KeywordLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(KeywordLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(KeywordSearchOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(KeywordLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(KeywordTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(KeywordSearchInput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(35, 35, 35))
        );

        SearchTabPanel.addTab("Keyword", Keyword);

        SemanticTitle.setText("Semantic Search");

        SemanticTextLabel.setText("Sentence similar to:");

        SemanticThresholdLabel.setText("Similarity Threshold:");

        SemanticThreshold.setMajorTickSpacing(10);
        SemanticThreshold.setMaximum(90);
        SemanticThreshold.setMinimum(20);
        SemanticThreshold.setMinorTickSpacing(1);
        SemanticThreshold.setPaintLabels(true);
        SemanticThreshold.setPaintTicks(true);
        SemanticThreshold.setSnapToTicks(true);
        SemanticThreshold.setToolTipText("");

        javax.swing.GroupLayout SemanticSearchInputLayout = new javax.swing.GroupLayout(SemanticSearchInput);
        SemanticSearchInput.setLayout(SemanticSearchInputLayout);
        SemanticSearchInputLayout.setHorizontalGroup(
            SemanticSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SemanticSearchInputLayout.createSequentialGroup()
                .addGap(0, 112, Short.MAX_VALUE)
                .addComponent(SemanticText, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
            .addGroup(SemanticSearchInputLayout.createSequentialGroup()
                .addGroup(SemanticSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SemanticSearchInputLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(SemanticSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SemanticThresholdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SemanticTextLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(SemanticSearchInputLayout.createSequentialGroup()
                        .addGap(109, 109, 109)
                        .addComponent(SemanticThreshold, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        SemanticSearchInputLayout.setVerticalGroup(
            SemanticSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SemanticSearchInputLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SemanticTextLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(SemanticText, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(SemanticThresholdLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(SemanticThreshold, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout SemanticLayout = new javax.swing.GroupLayout(Semantic);
        Semantic.setLayout(SemanticLayout);
        SemanticLayout.setHorizontalGroup(
            SemanticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SemanticLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(SemanticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SemanticSearchInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SemanticTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        SemanticLayout.setVerticalGroup(
            SemanticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SemanticLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(SemanticTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(SemanticSearchInput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        SearchTabPanel.addTab("Semantic", Semantic);

        SearchBoxLayer.add(SearchTabPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(292, -45, 800, 330));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        PDFViewPanel.add(SearchBoxLayer, gridBagConstraints);

        SearchInfoPanel.setBackground(new java.awt.Color(204, 204, 255));
        SearchInfoPanel.setMinimumSize(new java.awt.Dimension(1107, 30));

        SearchStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/duck_resized.gif"))); // NOI18N
        SearchStatus.setText("Status:");
        SearchStatus.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        SearchButton.setText("Search");
        SearchButton.setEnabled(false);
        SearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout SearchInfoPanelLayout = new javax.swing.GroupLayout(SearchInfoPanel);
        SearchInfoPanel.setLayout(SearchInfoPanelLayout);
        SearchInfoPanelLayout.setHorizontalGroup(
            SearchInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SearchInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ViewLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 816, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                .addComponent(SearchStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SearchButton)
                .addGap(12, 12, 12))
        );
        SearchInfoPanelLayout.setVerticalGroup(
            SearchInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SearchInfoPanelLayout.createSequentialGroup()
                .addGroup(SearchInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SearchStatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(SearchInfoPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(SearchInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(SearchInfoPanelLayout.createSequentialGroup()
                                .addComponent(SearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 12, Short.MAX_VALUE))
                            .addComponent(ViewLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.ipady = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 0);
        PDFViewPanel.add(SearchInfoPanel, gridBagConstraints);

        TextPanel.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                TextPanelMouseWheelMoved(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 883;
        gridBagConstraints.ipady = 497;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.4;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        PDFViewPanel.add(TextPanel, gridBagConstraints);

        ResultBoxLayer.setBackground(new java.awt.Color(204, 255, 204));

        ResultBoxMainPanel.setBackground(new java.awt.Color(204, 255, 204));

        ResultBoxLabel.setText("Search Results");

        javax.swing.GroupLayout ResultBoxMainPanelLayout = new javax.swing.GroupLayout(ResultBoxMainPanel);
        ResultBoxMainPanel.setLayout(ResultBoxMainPanelLayout);
        ResultBoxMainPanelLayout.setHorizontalGroup(
            ResultBoxMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ResultBoxMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ResultListScrollPane)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ResultBoxMainPanelLayout.createSequentialGroup()
                .addContainerGap(83, Short.MAX_VALUE)
                .addComponent(ResultBoxLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63))
        );
        ResultBoxMainPanelLayout.setVerticalGroup(
            ResultBoxMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ResultBoxMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ResultBoxLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ResultListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1129, Short.MAX_VALUE)
                .addGap(11, 11, 11))
        );

        ResultBoxLayer.setLayer(ResultBoxMainPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout ResultBoxLayerLayout = new javax.swing.GroupLayout(ResultBoxLayer);
        ResultBoxLayer.setLayout(ResultBoxLayerLayout);
        ResultBoxLayerLayout.setHorizontalGroup(
            ResultBoxLayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ResultBoxMainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        ResultBoxLayerLayout.setVerticalGroup(
            ResultBoxLayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ResultBoxMainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        NavigationMainPanel.setBackground(new java.awt.Color(255, 204, 204));

        NavigationLabel.setText("PDF Document Manger");

        NavigationOpenButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/open_resized.png"))); // NOI18N
        NavigationOpenButton.setText("Open");
        NavigationOpenButton.setEnabled(false);
        NavigationOpenButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        NavigationOpenButton.setIconTextGap(20);
        NavigationOpenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NavigationOpenButtonActionPerformed(evt);
            }
        });

        OpenedDocumentList.setModel(new javax.swing.AbstractListModel<String>() {
            public int getSize() { return documentListModel.size(); }
            public String getElementAt(int i) { return documentListModel.getElementAt(i); }
        });
        OpenedDocumentList.setFixedCellHeight(50);
        OpenedDocumentList.setVisibleRowCount(5);
        OpenedDocumentListScrollPane.setViewportView(OpenedDocumentList);
        OpenedDocumentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        NavigationSelectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/select_resized.png"))); // NOI18N
        NavigationSelectButton.setText("Select");
        NavigationSelectButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        NavigationSelectButton.setIconTextGap(25);
        NavigationSelectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NavigationSelectButtonActionPerformed(evt);
            }
        });

        NavigationViewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/view_resized.png"))); // NOI18N
        NavigationViewButton.setText("View");
        NavigationViewButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        NavigationViewButton.setIconTextGap(28);
        NavigationViewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NavigationViewButtonActionPerformed(evt);
            }
        });

        NavigationDeleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/delete_resized.png"))); // NOI18N
        NavigationDeleteButton.setText("Delete");
        NavigationDeleteButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        NavigationDeleteButton.setIconTextGap(25);
        NavigationDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NavigationDeleteButtonActionPerformed(evt);
            }
        });

        NavigationIconPanel.setPreferredSize(new java.awt.Dimension(30, 274));

        javax.swing.GroupLayout NavigationIconPanelLayout = new javax.swing.GroupLayout(NavigationIconPanel);
        NavigationIconPanel.setLayout(NavigationIconPanelLayout);
        NavigationIconPanelLayout.setHorizontalGroup(
            NavigationIconPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Icon0, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(Icon1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(Icon2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(Icon3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(Icon4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        NavigationIconPanelLayout.setVerticalGroup(
            NavigationIconPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NavigationIconPanelLayout.createSequentialGroup()
                .addComponent(Icon0, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Icon1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Icon2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Icon3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Icon4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout NavigationMainPanelLayout = new javax.swing.GroupLayout(NavigationMainPanel);
        NavigationMainPanel.setLayout(NavigationMainPanelLayout);
        NavigationMainPanelLayout.setHorizontalGroup(
            NavigationMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NavigationMainPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(NavigationMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(NavigationViewButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(NavigationSelectButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(NavigationOpenButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(NavigationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(NavigationDeleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, NavigationMainPanelLayout.createSequentialGroup()
                        .addComponent(NavigationIconPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(OpenedDocumentListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        NavigationMainPanelLayout.setVerticalGroup(
            NavigationMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NavigationMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(NavigationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(NavigationOpenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(NavigationMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(OpenedDocumentListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE)
                    .addComponent(NavigationIconPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(NavigationSelectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(NavigationViewButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(NavigationDeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(171, 171, 171))
        );

        NavigationBoxLayer.setLayer(NavigationMainPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout NavigationBoxLayerLayout = new javax.swing.GroupLayout(NavigationBoxLayer);
        NavigationBoxLayer.setLayout(NavigationBoxLayerLayout);
        NavigationBoxLayerLayout.setHorizontalGroup(
            NavigationBoxLayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NavigationBoxLayerLayout.createSequentialGroup()
                .addComponent(NavigationMainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        NavigationBoxLayerLayout.setVerticalGroup(
            NavigationBoxLayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NavigationBoxLayerLayout.createSequentialGroup()
                .addComponent(NavigationMainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout MainPanelLayout = new javax.swing.GroupLayout(MainPanel);
        MainPanel.setLayout(MainPanelLayout);
        MainPanelLayout.setHorizontalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addComponent(NavigationBoxLayer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PDFViewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1300, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ResultBoxLayer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        MainPanelLayout.setVerticalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ResultBoxLayer)
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PDFViewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(NavigationBoxLayer))
                        .addContainerGap())))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1858, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1192, Short.MAX_VALUE)
                .addContainerGap())
        );

        setBounds(0, 0, 1880, 1234);
    }// </editor-fold>//GEN-END:initComponents

    private void TextPanelMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_TextPanelMouseWheelMoved

    }//GEN-LAST:event_TextPanelMouseWheelMoved

    private void SemanticSelectorButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_SemanticSelectorButtonActionPerformed
        SearchTabPanel.setSelectedIndex(2);
    }// GEN-LAST:event_SemanticSelectorButtonActionPerformed

    private void KeywordSelectorButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_KeywordSelectorButtonActionPerformed
        SearchTabPanel.setSelectedIndex(1);
    }// GEN-LAST:event_KeywordSelectorButtonActionPerformed

    private void LexicalSelectorButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_LexicalSelectorButtonActionPerformed
        SearchTabPanel.setSelectedIndex(0);
    }// GEN-LAST:event_LexicalSelectorButtonActionPerformed

    private void SearchBoxActivationButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_SearchBoxActivationButtonActionPerformed
        SearchBoxLayer.setVisible(!SearchBoxLayer.isVisible());
        SearchButton.setVisible(!SearchButton.isVisible());
        if (searchBoxButtonFlag) {
            SearchBoxActivationButton
                    .setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/down_arrow_resized.png")));
        } else {
            SearchBoxActivationButton
                    .setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/up_arrow_resized.png")));
        }
        searchBoxButtonFlag = !searchBoxButtonFlag;
    }// GEN-LAST:event_SearchBoxActivationButtonActionPerformed

    private void SearchButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_SearchButtonActionPerformed
        clearResults();
        lock();
        pdfManager.resultHandler.setCounter(numFileSelected);
        int mode = SearchTabPanel.getSelectedIndex();

        switch (mode) {
            case 0: // lexical
                List<String> targets = new ArrayList<String>();
                List<String> connectors = new ArrayList<String>();
                if (!LexicalText1.getText().isEmpty()) {
                    targets.add(LexicalText1.getText());
                    connectors.add("OR");
                }
                if (!LexicalText2.getText().isEmpty()) {
                    targets.add(LexicalText2.getText());
                    connectors.add(String.valueOf(LexicalConnector2.getSelectedItem()));
                }
                if (!LexicalText3.getText().isEmpty()) {
                    targets.add(LexicalText3.getText());
                    connectors.add(String.valueOf(LexicalConnector3.getSelectedItem()));
                }
                if (!LexicalText4.getText().isEmpty()) {
                    targets.add(LexicalText4.getText());
                    connectors.add(String.valueOf(LexicalConnector4.getSelectedItem()));
                }
                if (!LexicalText5.getText().isEmpty()) {
                    targets.add(LexicalText5.getText());
                    connectors.add(String.valueOf(LexicalConnector5.getSelectedItem()));
                }
                String scope = String.valueOf(LexicalRange.getSelectedItem());

                pdfManager.resultHandler.setCounter(numFileSelected);
                for (int i = 0; i < selectedDocuments.size(); i++) {
                    if (selectedDocuments.get(i)) {
                        pdfManager.searchLexical(i, targets.toArray(new String[0]), connectors.toArray(new String[0]),
                                scope);
                    }
                }
                break;

            case 1: // keyword
                String keywordText = KeywordText.getText();
                String pos = String.valueOf(KeywordPOS.getSelectedItem());
                if (pos.equals("NONE")) {
                    pos = "";
                }
                boolean check = KeywordSynonyms.isSelected();

                for (int i = 0; i < selectedDocuments.size(); i++) {
                    if (selectedDocuments.get(i)) {
                        pdfManager.searchKeyword(i, keywordText, pos, check);
                    }
                }
                break;

            case 2: // semantic
                String semanticText = SemanticText.getText();
                int threshold = SemanticThreshold.getValue();
                pdfManager.resultHandler.setCounter(numFileSelected);
                for (int i = 0; i < selectedDocuments.size(); i++) {
                    if (selectedDocuments.get(i)) {
                        pdfManager.searchSemantic(i, semanticText, threshold);
                    }
                }
                break;
        }

    }// GEN-LAST:event_SearchButtonActionPerformed

    private void ResultBoxActivationButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_ResultBoxActivationButtonActionPerformed
        ResultBoxLayer.setVisible(!ResultBoxLayer.isVisible());
        if (ResultBoxButtonFlag) {
            ResultBoxActivationButton
                    .setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/left_arrow_resized.png")));
        } else {
            ResultBoxActivationButton
                    .setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/right_arrow_resized.png")));
        }
        ResultBoxButtonFlag = !ResultBoxButtonFlag;
    }// GEN-LAST:event_ResultBoxActivationButtonActionPerformed

    private void NavigationBoxActivationButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_NavigationBoxActivationButtonActionPerformed
        NavigationBoxLayer.setVisible(!NavigationBoxLayer.isVisible());
        if (NavigationBoxButtonFlag) {
            NavigationBoxActivationButton
                    .setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/right_arrow_resized.png")));
        } else {
            NavigationBoxActivationButton
                    .setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/left_arrow_resized.png")));
        }
        NavigationBoxButtonFlag = !NavigationBoxButtonFlag;
    }// GEN-LAST:event_NavigationBoxActivationButtonActionPerformed

    private void NavigationOpenButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_NavigationOpenButtonActionPerformed
        final JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filepath = selectedFile.getAbsolutePath();
            if (PDFHandler.isFileValid(filepath)) {
                lock();
                openDocument(filepath);
                documentListModel.addElement(PDFManager.documents.get(documentListModel.size()).getTitle());
            } else {
                JOptionPane.showMessageDialog(null, "Invalid file, please select a valid PDF Document!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
    }// GEN-LAST:event_NavigationOpenButtonActionPerformed

    private void NavigationSelectButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_NavigationSelectButtonActionPerformed
        int index = OpenedDocumentList.getSelectedIndex();
        if (index == -1) {
            return;
        }
        selectedDocuments.set(index, !selectedDocuments.get(index));
        boolean val = selectedDocuments.get(index);
        if (val) {
            numFileSelected++;
        } else {
            numFileSelected--;
        }
        switch (index) {
            case 0:
                if (val) {
                    Icon0.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/check_resized.png")));
                } else {
                    Icon0.setIcon(null);
                }
                break;
            case 1:
                if (val) {
                    Icon1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/check_resized.png")));
                } else {
                    Icon1.setIcon(null);
                }
                break;
            case 2:
                if (val) {
                    Icon2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/check_resized.png")));
                } else {
                    Icon2.setIcon(null);
                }
                break;
            case 3:
                if (val) {
                    Icon3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/check_resized.png")));
                } else {
                    Icon3.setIcon(null);
                }
                break;
            case 4:
                if (val) {
                    Icon4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/check_resized.png")));
                } else {
                    Icon4.setIcon(null);
                }
                break;
        }
        updateSearchButton();
    }// GEN-LAST:event_NavigationSelectButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowClosing
        try {
            pdfManager.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }// GEN-LAST:event_formWindowClosing

    private void NavigationDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_NavigationDeleteButtonActionPerformed
        int index = OpenedDocumentList.getSelectedIndex();
        if (index == -1) {
            return;
        }
        boolean val = selectedDocuments.get(index);
        if (val) {
            numFileSelected--;
        }
        updateSearchButton();
        lock();
        pdfManager.closeDocument(index);
        documentListModel.remove(index);
        selectedDocuments.remove(index);
        selectedDocuments.add(false);
        if (selectedDocuments.get(0)) {
            Icon0.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/check_resized.png")));
        } else {
            Icon0.setIcon(null);
        }
        if (selectedDocuments.get(1)) {
            Icon1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/check_resized.png")));
        } else {
            Icon1.setIcon(null);
        }
        if (selectedDocuments.get(2)) {
            Icon2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/check_resized.png")));
        } else {
            Icon2.setIcon(null);
        }
        if (selectedDocuments.get(3)) {
            Icon3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/check_resized.png")));
        } else {
            Icon3.setIcon(null);
        }
        if (selectedDocuments.get(4)) {
            Icon4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/check_resized.png")));
        } else {
            Icon4.setIcon(null);
        }
    }// GEN-LAST:event_NavigationDeleteButtonActionPerformed

    private void NavigationViewButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_NavigationViewButtonActionPerformed
        int index = OpenedDocumentList.getSelectedIndex();
        if (index == -1) {
            return;
        }
        switchView(index);
    }// GEN-LAST:event_NavigationViewButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default
         * look and feel.
         * For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */

        try {
            javax.swing.UIManager.setLookAndFeel(new FlatCyanLightIJTheme());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new UserInterface().setVisible(true);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Icon0;
    private javax.swing.JLabel Icon1;
    private javax.swing.JLabel Icon2;
    private javax.swing.JLabel Icon3;
    private javax.swing.JLabel Icon4;
    private javax.swing.JPanel Keyword;
    private javax.swing.JComboBox<String> KeywordPOS;
    private javax.swing.JPanel KeywordSearchInput;
    private javax.swing.JPanel KeywordSearchOptions;
    private javax.swing.JButton KeywordSelectorButton;
    private javax.swing.JCheckBox KeywordSynonyms;
    private javax.swing.JTextField KeywordText;
    private javax.swing.JLabel KeywordTextLabel;
    private javax.swing.JLabel KeywordTitle;
    private javax.swing.JLabel KeywowrdPOSLabel;
    private javax.swing.JPanel Lexical;
    private javax.swing.JComboBox<String> LexicalConnector2;
    private javax.swing.JComboBox<String> LexicalConnector3;
    private javax.swing.JComboBox<String> LexicalConnector4;
    private javax.swing.JComboBox<String> LexicalConnector5;
    private javax.swing.JComboBox<String> LexicalRange;
    private javax.swing.JLabel LexicalRangeLabel;
    private javax.swing.JPanel LexicalSearchInput;
    private javax.swing.JPanel LexicalSearchOptions;
    private javax.swing.JButton LexicalSelectorButton;
    private javax.swing.JTextField LexicalText1;
    private javax.swing.JTextField LexicalText2;
    private javax.swing.JTextField LexicalText3;
    private javax.swing.JTextField LexicalText4;
    private javax.swing.JTextField LexicalText5;
    private javax.swing.JLabel LexicalTitle;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JButton NavigationBoxActivationButton;
    private javax.swing.JLayeredPane NavigationBoxLayer;
    private javax.swing.JButton NavigationDeleteButton;
    private javax.swing.JPanel NavigationIconPanel;
    private javax.swing.JLabel NavigationLabel;
    private javax.swing.JPanel NavigationMainPanel;
    private javax.swing.JButton NavigationOpenButton;
    private javax.swing.JButton NavigationSelectButton;
    private javax.swing.JButton NavigationViewButton;
    private javax.swing.JList<String> OpenedDocumentList;
    private javax.swing.JScrollPane OpenedDocumentListScrollPane;
    private javax.swing.JPanel PDFViewPanel;
    private javax.swing.JButton ResultBoxActivationButton;
    private javax.swing.JLabel ResultBoxLabel;
    private javax.swing.JLayeredPane ResultBoxLayer;
    private javax.swing.JPanel ResultBoxMainPanel;
    private javax.swing.JScrollPane ResultListScrollPane;
    private javax.swing.JButton SearchBoxActivationButton;
    private javax.swing.JLayeredPane SearchBoxLayer;
    private javax.swing.JButton SearchButton;
    private javax.swing.JPanel SearchInfoPanel;
    private javax.swing.JLabel SearchStatus;
    private javax.swing.JTabbedPane SearchTabPanel;
    private javax.swing.JPanel Semantic;
    private javax.swing.JPanel SemanticSearchInput;
    private javax.swing.JButton SemanticSelectorButton;
    private javax.swing.JTextField SemanticText;
    private javax.swing.JLabel SemanticTextLabel;
    private javax.swing.JSlider SemanticThreshold;
    private javax.swing.JLabel SemanticThresholdLabel;
    private javax.swing.JLabel SemanticTitle;
    private javax.swing.JScrollPane TextPanel;
    private javax.swing.JLabel TextSearchOptions;
    private javax.swing.JLabel ViewLabel;
    // End of variables declaration//GEN-END:variables
}
