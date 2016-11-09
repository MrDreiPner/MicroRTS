/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.frontend;

import ai.core.AI;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import rts.units.UnitTypeTable;
import tournaments.FixedOpponentsTournament;
import tournaments.RoundRobinTournament;

/**
 *
 * @author santi
 */
public class FETournamentPane extends JPanel {
    public static final String TOURNAMENT_ROUNDROBIN = "Round Robin";
    public static final String TOURNAMENT_FIXED_OPPONENTS = "Fixed Opponents";
    
    JComboBox tournamentTypeComboBox = null;
    
    Object availableAIsListNames[] = null;
    JList availableAIsList = null;
    DefaultListModel selectedAIsListModel = null;
    JList selectedAIsList = null;
    DefaultListModel opponentAIsListModel = null;
    JList opponentAIsList = null;
    JButton opponentAddButton = null;
    JButton opponentRemoveButton = null;
    
    JFileChooser mapFileChooser = new JFileChooser();
    JList mapList = null;
    DefaultListModel mapListModel = null;
    
    JFormattedTextField iterationsField = null;
    JFormattedTextField maxGameLengthField = null;
    JFormattedTextField timeBudgetField = null;
    JFormattedTextField iterationsBudgetField = null;
    
    JCheckBox fullObservabilityCheckBox = null;
    JCheckBox selfMatchesCheckBox = null;
    JCheckBox timeoutCheckBox = null;
    JCheckBox gcCheckBox = null;
    
    public FETournamentPane() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        {
            String tournamentTypes[] = {TOURNAMENT_ROUNDROBIN, TOURNAMENT_FIXED_OPPONENTS};
            tournamentTypeComboBox = new JComboBox(tournamentTypes);
            tournamentTypeComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
            tournamentTypeComboBox.setAlignmentY(Component.TOP_ALIGNMENT);
            tournamentTypeComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    JComboBox combo = (JComboBox)e.getSource();
                    if (combo.getSelectedIndex()==1) {
                        opponentAIsList.setEnabled(true);
                        opponentAddButton.setEnabled(true);
                        opponentRemoveButton.setEnabled(true);
                    } else {
                        opponentAIsList.setEnabled(false);
                        opponentAddButton.setEnabled(false);
                        opponentRemoveButton.setEnabled(false);
                    }
                }
            });
            tournamentTypeComboBox.setMaximumSize(new Dimension(300,24));
            add(tournamentTypeComboBox);
        }
        
        {
            JPanel p1 = new JPanel();
            p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
            
            {
                JPanel p1left = new JPanel();
                p1left.setLayout(new BoxLayout(p1left, BoxLayout.Y_AXIS));
                p1left.add(new JLabel("Available AIs"));

                availableAIsListNames = new Object[FEStatePane.AIs.length];
                for(int i = 0;i<FEStatePane.AIs.length;i++) {
                    availableAIsListNames[i] = FEStatePane.AIs[i];
                }
                
                availableAIsList = new JList(availableAIsListNames);
                availableAIsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                availableAIsList.setLayoutOrientation(JList.VERTICAL);
                availableAIsList.setVisibleRowCount(-1);
                JScrollPane listScroller = new JScrollPane(availableAIsList);
                listScroller.setPreferredSize(new Dimension(200, 200));
                p1left.add(listScroller);
                p1.add(p1left);
            }
            {
                JPanel p1center = new JPanel();
                p1center.setLayout(new BoxLayout(p1center, BoxLayout.Y_AXIS));
                p1center.add(new JLabel("Selected AIs"));

                selectedAIsListModel = new DefaultListModel();
                selectedAIsList = new JList(selectedAIsListModel);
                selectedAIsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                selectedAIsList.setLayoutOrientation(JList.VERTICAL);
                selectedAIsList.setVisibleRowCount(-1);
                JScrollPane listScroller = new JScrollPane(selectedAIsList);
                listScroller.setPreferredSize(new Dimension(200, 200));
                p1center.add(listScroller);
                JButton add = new JButton("+");
                add.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e)
                    {
                        int selected[] = availableAIsList.getSelectedIndices();
                        for(int idx:selected) {
                            selectedAIsListModel.addElement(availableAIsList.getModel().getElementAt(idx));
                        }
                    }
                });
                p1center.add(add);
                JButton remove = new JButton("-");
                remove.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e)
                    {
                        int selectedIndex = selectedAIsList.getSelectedIndex();
                        if (selectedIndex>=0) selectedAIsListModel.remove(selectedIndex);
                    }
                });
                p1center.add(remove);
                p1.add(p1center);
            }
            {
                JPanel p1right = new JPanel();
                p1right.setLayout(new BoxLayout(p1right, BoxLayout.Y_AXIS));
                p1right.add(new JLabel("Opponent AIs"));

                opponentAIsListModel = new DefaultListModel();
                opponentAIsList = new JList(opponentAIsListModel);
                opponentAIsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                opponentAIsList.setLayoutOrientation(JList.VERTICAL);
                opponentAIsList.setVisibleRowCount(-1);
                JScrollPane listScroller = new JScrollPane(opponentAIsList);
                listScroller.setPreferredSize(new Dimension(200, 200));
                p1right.add(listScroller);
                opponentAddButton = new JButton("+");
                opponentAddButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e)
                    {
                        int selected[] = availableAIsList.getSelectedIndices();
                        for(int idx:selected) {
                            opponentAIsListModel.addElement(availableAIsList.getModel().getElementAt(idx));
                        }
                    }
                });
                p1right.add(opponentAddButton);
                opponentRemoveButton = new JButton("-");
                opponentRemoveButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e)
                    {
                        int selectedIndex = opponentAIsList.getSelectedIndex();
                        if (selectedIndex>=0) opponentAIsListModel.remove(selectedIndex);
                    }
                });
                p1right.add(opponentRemoveButton);
                p1.add(p1right);

                opponentAIsList.setEnabled(false);
                opponentAddButton.setEnabled(false);
                opponentRemoveButton.setEnabled(false);
            }
            add(p1);
        }
        add(new JSeparator(SwingConstants.HORIZONTAL));
                
        {
            JPanel p2 = new JPanel();
            p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));

            {
                JPanel p2maps = new JPanel();
                p2maps.setLayout(new BoxLayout(p2maps, BoxLayout.Y_AXIS));
                p2maps.add(new JLabel("Maps"));
                mapListModel = new DefaultListModel();
                mapList = new JList(mapListModel);
                mapList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                mapList.setLayoutOrientation(JList.VERTICAL);
                mapList.setVisibleRowCount(-1);
                JScrollPane listScroller = new JScrollPane(mapList);
                listScroller.setPreferredSize(new Dimension(200, 100));
                p2maps.add(listScroller);
                JButton add = new JButton("+");
                add.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e)
                    {
                        int returnVal = mapFileChooser.showOpenDialog((Component)null);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = mapFileChooser.getSelectedFile();
                            try {
                                mapListModel.addElement(file.getPath());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                       }
                    }
                });
                p2maps.add(add);
                JButton remove = new JButton("-");
                remove.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e)
                    {
                        int selected = mapList.getSelectedIndex();
                        if (selected>=0) {
                            mapListModel.remove(selected);
                        }
                    }
                });
                p2maps.add(remove);
                
                p2.add(p2maps);
            }
            p2.add(new JSeparator(SwingConstants.VERTICAL));

            {
                JPanel p2left = new JPanel();
                p2left.setLayout(new BoxLayout(p2left, BoxLayout.Y_AXIS));
                
                // N, maxgame length, time budget, iterations budget
                iterationsField = FEStatePane.addTextField(p2left,"Iterations:", "10", 4);
                maxGameLengthField = FEStatePane.addTextField(p2left,"Max Game Length:", "3000", 4);
                timeBudgetField = FEStatePane.addTextField(p2left,"Time Budget:", "100", 5);
                iterationsBudgetField = FEStatePane.addTextField(p2left,"Iterations Budget:", "-1", 8);
                
                p2.add(p2left);            
            }            
            p2.add(new JSeparator(SwingConstants.VERTICAL));

            {
                JPanel p2right = new JPanel();
                p2right.setLayout(new BoxLayout(p2right, BoxLayout.Y_AXIS));
                
                fullObservabilityCheckBox = new JCheckBox("Full Obsservability");
                fullObservabilityCheckBox.setSelected(true);
                p2right.add(fullObservabilityCheckBox);
                selfMatchesCheckBox = new JCheckBox("Include self-play matches");
                selfMatchesCheckBox.setSelected(false);
                p2right.add(selfMatchesCheckBox);
                timeoutCheckBox = new JCheckBox("Game over if AI times out");
                timeoutCheckBox.setSelected(true);
                p2right.add(timeoutCheckBox);
                gcCheckBox = new JCheckBox("Call garbage collector right before each AI call");
                gcCheckBox.setSelected(false);                
                p2right.add(gcCheckBox);
                p2.add(p2right);
            }            
            add(p2);
        }
        
        JButton run = new JButton("Run Tournament");
        add(run);        
        run.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try {
                    // get all the necessary info:
                    UnitTypeTable utt = new UnitTypeTable();
                    String tournamentType = (String)tournamentTypeComboBox.getSelectedItem();
                    List<AI> selectedAIs = new ArrayList<>();
                    List<AI> opponentAIs = new ArrayList<>();
                    List<String> maps = new ArrayList<>();
                    for(int i = 0;i<selectedAIsListModel.getSize();i++) {
                        Class c = (Class)selectedAIsListModel.get(i);
                        Constructor cons = c.getConstructor(UnitTypeTable.class);
                        selectedAIs.add((AI)cons.newInstance(utt));
                    }
                    for(int i = 0;i<opponentAIsListModel.getSize();i++) {
                        Class c = (Class)opponentAIsListModel.get(i);
                        Constructor cons = c.getConstructor(UnitTypeTable.class);
                        opponentAIs.add((AI)cons.newInstance(utt));
                    }
                    for(int i = 0;i<mapListModel.getSize();i++) {
                        String mapname = (String)mapListModel.getElementAt(i);
                        maps.add(mapname);
                    }
                    
                    int iterations = Integer.parseInt(iterationsField.getText());
                    int maxGameLength = Integer.parseInt(maxGameLengthField.getText());
                    int timeBudget = Integer.parseInt(timeBudgetField.getText());
                    int iterationsBudget = Integer.parseInt(iterationsBudgetField.getText());
                    
                    boolean fullObservability = fullObservabilityCheckBox.isSelected();
                    boolean selfMatches = selfMatchesCheckBox.isSelected();
                    boolean timeOutCheck = timeoutCheckBox.isSelected();
                    boolean gcCheck = gcCheckBox.isSelected();

                    String prefix = "tournament_";
                    int idx = 0;
                    String sufix = ".tsv";
                    File file;
                    do {
                        idx++;
                        file = new File(prefix + idx + sufix);
                    }while(file.exists());
                    Writer writer = new FileWriter(file);
                    Writer writerProgress = new OutputStreamWriter(System.out);
                                        
                    if (tournamentType.equals(TOURNAMENT_ROUNDROBIN)) {
                        RoundRobinTournament.runTournament(selectedAIs, maps, 
                                                           iterations, maxGameLength, timeBudget, iterationsBudget,
                                                           fullObservability, selfMatches, timeOutCheck, gcCheck, 
                                                           utt, writer, writerProgress);
                    } else if (tournamentType.equals(TOURNAMENT_FIXED_OPPONENTS)) {
                        FixedOpponentsTournament.runTournament(selectedAIs, opponentAIs, maps, 
                                                           iterations, maxGameLength, timeBudget, iterationsBudget,
                                                           fullObservability, timeOutCheck, gcCheck, 
                                                           utt, writer, writerProgress);
                        
                    }
                    writer.close();
                }catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}