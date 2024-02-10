package com.searcher;

import javax.swing.*;
import java.awt.*;


public class GUIView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        // Create the main window
        JFrame frame = new JFrame("PDF Viewer Like Interface");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 800);

        // Scroll pane to hold the pages
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Panel to hold multiple pages
        JPanel pagesPanel = new JPanel();
        pagesPanel.setLayout(new BoxLayout(pagesPanel, BoxLayout.Y_AXIS));

        // Split the text into pages using '\p' as a separator
        String text = "\n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                "Department of Computing \n" + //
                " \n" + //
                "COMP4913 Capstone Project \n" + //
                "Handbook \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                "September 2023 \n" + //
                " \n" + //
                "This Capstone Project Handbook is subject to review and changes which the subject offering Department can \n" + //
                "decide to make from time to time. Students will be informed of the changes as and when appropriate.  \\p\n" + //
                " \n" + //
                "Table of Contents \n" + //
                " \n" + //
                "Part 1: Capstone Project Timeline ....................................................................................................................... 1 \n" + //
                " \n" + //
                "Part 2: Project Selection and Assignment ............................................................................................................ 2 \n" + //
                " \n" + //
                "2.1  Proposed Projects, Rationale and Publicity .............................................................................................. 2 \n" + //
                "2.2 Project Information Session ...................................................................................................................... 2 \n" + //
                "2.3 Project Selection and Allocation............................................................................................................... 2 \n" + //
                "2.3.1 First Round Allocation ................................................................................................................ 2 \n" + //
                "2.3.2 Second Round Allocation ............................................................................................................ 3 \n" + //
                "2.3.3 Final Round Allocation ............................................................................................................... 3 \n" + //
                "2.3.4 Special Arrangement for Previous Withdrawal Cases ................................................................ 3 \n" + //
                "2.3.5 Interview ..................................................................................................................................... 3 \n" + //
                " \n" + //
                "Part 3: Change of Project Title ............................................................................................................................ 4 \n" + //
                " \n" + //
                "Part 4: Project Proposal Submission .................................................................................................................... 4 \n" + //
                " \n" + //
                "4.1 Submission and Schedule ......................................................................................................................... 4 \n" + //
                "4.2 General Requirements .............................................................................................................................. 4 \n" + //
                " \n" + //
                "Part 5: Document Submission ............................................................................................................................. 5 \n" + //
                " \n" + //
                "5.1 Meeting Notes ........................................................................................................................................... 5 \n" + //
                "5.2 Interim Report ........................................................................................................................................... 5 \n" + //
                "5.3 Final Report .............................................................................................................................................. 6 \n" + //
                "5. 4 One-page Project Summary ...................................................................................................................... 6 \n" + //
                "5.5 Presentation Video .................................................................................................................................... 6 \n" + //
                "5.6 Presentation File ....................................................................................................................................... 7 \n" + //
                "5.7 Additional Files ........................................................................................................................................ 7 \n" + //
                "5.8 Individual Work ........................................................................................................................................ 7 \n" + //
                "5.9 Plagiarism ................................................................................................................................................. 7 \n" + //
                " \n" + //
                "Part 6: Presentations ............................................................................................................................................ 8 \n" + //
                " \n" + //
                "6.1 Objectives and General Information ......................................................................................................... 8 \n" + //
                "6.2 Before the Presentation ............................................................................................................................. 8 \n" + //
                "6.3 During the Presentation ............................................................................................................................ 8 \n" + //
                "6.4 After the Presentation ............................................................................................................................... 9 \n" + //
                " \n" + //
                "Part 7: Best Project Award Competition ............................................................................................................. 9 \n" + //
                " \n" + //
                "Part 8: Intellectual Property ................................................................................................................................. 9 \n" + //
                " \n" + //
                "Part 9: Assessment ............................................................................................................................................... 9 \n" + //
                " \n" + //
                "9.1 Criterion Referencing Assessment and Grading ..................................................................................... 10 \n" + //
                "9.1.1 Project Proposal ........................................................................................................................ 10 \n" + //
                "9.1.2 Interim Assessment ................................................................................................................... 10 \n" + //
                "9.1.3 Final Assessment ....................................................................................................................... 11 \n" + //
                "9.2 Penalty .................................................................................................................................................... 13 \n" + //
                " \n" + //
                " \\p\n" + //
                "Part 10: Structure and Format Requirements of Proposal and Reports ............................................................. 14 \n" + //
                "10.1 Proposal .................................................................................................................................................. 14 \n" + //
                "10.1.1 Sample Structure of the Proposal .............................................................................................. 14 \n" + //
                "10.1.2 Formatting Requirements of the Proposal ................................................................................. 15 \n" + //
                "10.2 Interim/Final Report ............................................................................................................................... 16 \n" + //
                "10.2.1 Organization of the Reports ...................................................................................................... 16 \n" + //
                "10.2.2 Cover and Formatting Requirements of the Reports ................................................................. 16 \n" + //
                "10.2.3 Submission Requirements of the Reports ................................................................................. 17 \n" + //
                "Appendix ........................................................................................................................................................... 18 \n" + //
                "Guidelines for Students on the Use of Generative Artificial Intelligence 22 \\p\n" + //
                "Preface \n" + //
                " \n" + //
                "To help students develop their generic competencies, as well as prepare them for professional \n" + //
                "practice in the workplace, for further academic pursuits and for lifelong learning, the University \n" + //
                "has a mandatory Capstone Project learning experience requirement for all four-year undergraduate \n" + //
                "programmes. Students are expected to consolidate their learning experiences accumulated over \n" + //
                "their entire undergraduate study in the Capstone Project. \n" + //
                " \n" + //
                "This handbook aims to provide you with a better understanding of the Capstone Project, including \n" + //
                "the allocation, proposal and report requirements, presentation arrangements, etc.\\p \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                " \n" + //
                "Part 1: Capstone Project Timeline \n" + //
                " \n" + //
                "3 Apr 2023\n" + //
                "Information Session\n" + //
                "7 - 17 Apr 2023\n" + //
                "Project Selection (First Round)\n" + //
                "8 - 10 May 2023\n" + //
                "Interview (First Round)\n" + //
                "26 - 28 May 2023\n" + //
                "Project Selection (Second Round)\n" + //
                "13 - 18 Jun 2023\n" + //
                "Project Selection (Final Round)\n" + //
                "27 Oct 2023\n" + //
                "Project Proposal Submission\n" + //
                "15 Jan 2024\n" + //
                "Interim Report and Presentation Video Submission\n" + //
                "12 Apr 2024\n" + //
                "Final Report Submission\n" + //
                "20 - 24 Apr 2024\n" + //
                "Final Presentation\n" + //
                "7 Jun 2024 (TBC)\n" + //
                "Best Project Award Competition (Selected Projects Only)\n" + //
                "  \n" + //
                "1 \\p\n" + //
                " \n" + //
                " Part 2: Project Selection and Assignment \n" + //
                " \n" + //
                "2.1  Proposed Projects, Rationale and Publicity \n" + //
                " \n" + //
                "i. Each faculty member will be assigned with an appropriate capstone project \n" + //
                "supervision loading by the Department; \n" + //
                "ii. Faculty members will prepare a list of proposed projects and specify the quota for \n" + //
                "each project, together with a brief description and expected outcomes of the project; \n" + //
                "and \n" + //
                "iii. The list of project proposals will be assembled and posted on the Intranet before each \n" + //
                "project selection round. Please refer to the Capstone Project Timeline for details. \n" + //
                " \n" + //
                " \n" + //
                "2.2 Project Information Session \n" + //
                " \n" + //
                "i. During the project information session, some project supervisors will introduce their \n" + //
                "proposed projects to the students; and \n" + //
                "ii. List of projects with introductory session will be given in the rundown posted on the \n" + //
                "Intranet before the information session. \n" + //
                " \n" + //
                " \n" + //
                "2.3 Project Selection and Allocation \n" + //
                " \n" + //
                "2.3.1 First Round Allocation \n" + //
                " \n" + //
                "i. Students should submit a project preference form indicating 10 interested projects, in \n" + //
                "descending order, via the Capstone Project Selection System; \n" + //
                " \n" + //
                "Students Projects to Choose \n" + //
                "BSc Computing/ IT Category A projects only \n" + //
                "BSc EIS Category B projects only \n" + //
                "BSc Information Security Category C projects only \n" + //
                "BSc FinTech/ FinTech and AI Category D projects only \n" + //
                " \n" + //
                "ii. During the interview period (refer to 2.3.5 for more details about interviews), students \n" + //
                "may resubmit their project preference form if they would need to re-prioritize or delete \n" + //
                "some of their project choices. However, NO new projects can be added; \n" + //
                "iii. Similar to the well-known JUPAS allocation model, a fair computer program will be \n" + //
                "run to allocate capstone projects to students; and \n" + //
                "iv. Project allocation result and remaining projects for next round will be posted on the \n" + //
                "Intranet 1 week after the interview period. \n" + //
                " \n" + //
                "  \n" + //
                "2 \n" + //
                " \n" + //
                "";
        String[] pages = text.split("\\\\p");

        // Create a panel for each page
        for (String pageText : pages) {
            JPanel pagePanel = createPage(pageText);
            pagesPanel.add(pagePanel);
            pagesPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between pages
        }

        scrollPane.setViewportView(pagesPanel);
        frame.add(scrollPane);

        // Display the window
        frame.setVisible(true);
    }

    private static JPanel createPage(String text) {
        JPanel pagePanel = new JPanel();
        pagePanel.setLayout(new BorderLayout());
        pagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        pagePanel.setPreferredSize(new Dimension(595, 842)); // A4 size in pixels at 72 PPI

        JTextArea textArea = new JTextArea();
        textArea.setText(text);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);

        pagePanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return pagePanel;
    }
}
