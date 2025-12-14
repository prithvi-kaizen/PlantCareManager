// comments are written by the team to navigate the code easily
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

class Plant {
    private String name;
    private String species;
    private int intervalDays;
    private Date lastWatered;
    private String notes;

    public Plant(String name, String species, int intervalDays) {
        this.name = name;
        this.species = species;
        this.intervalDays = intervalDays;
        this.lastWatered = null;
        this.notes = "";
    }

    public String getName() { return name; }
    public String getSpecies() { return species; }
    public int getIntervalDays() { return intervalDays; }
    public Date getLastWatered() { return lastWatered; }
    public String getNotes() { return notes; }

    public void setLastWatered(Date d) { lastWatered = d; }
    public void setNotes(String n) { notes = n; }
    public void setIntervalDays(int i) { intervalDays = i; }

    public String toString() {
        return name + " (" + species + ")";
    }

    public String detailString() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String lw = (lastWatered == null) ? "Never" : f.format(lastWatered);
        return "<html><b>" + name + "</b><br>Species: " + species
             + "<br>Water every: " + intervalDays + " day(s)"
             + "<br>Last watered: " + lw + "</html>";
    }
}

public class PlantCareManagerSimple extends JFrame {
    private DefaultListModel<Plant> model;
    private JList<Plant> list;
    private JLabel details;
    private JTextArea notes;
    private JComboBox<Integer> intervalBox;

    public PlantCareManagerSimple() {
        super("Plant Care Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 480);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        Container c =getContentPane();
        c.setLayout(new BorderLayout(8, 8));
        c.setBackground(Color.WHITE);

        // left part : plant list
        model =new DefaultListModel<>();
        list =new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroll =new JScrollPane(list);
        listScroll.setBorder(new TitledBorder("My Plants"));
        listScroll.setPreferredSize(new Dimension(320, 0));
        c.add(listScroll, BorderLayout.WEST);

        // center part: details + notes
        JPanel center = new JPanel(new BorderLayout(6,6));
        center.setBorder(new EmptyBorder(8,8,8,8));

        details = new JLabel("Select a plant to see details");
        details.setVerticalAlignment(SwingConstants.TOP);
        details.setBorder(new LineBorder(Color.LIGHT_GRAY));
        details.setPreferredSize(new Dimension(0, 110));
        center.add(details, BorderLayout.NORTH);

        notes = new JTextArea();
        notes.setLineWrap(true);
        notes.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notes);
        notesScroll.setBorder(new TitledBorder("Notes / Care Log"));
        center.add(notesScroll, BorderLayout.CENTER);

        JButton saveNotesBtn = new JButton("Save Notes");
        JPanel saveP = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveP.add(saveNotesBtn);
        center.add(saveP, BorderLayout.SOUTH);

        c.add(center, BorderLayout.CENTER);

        // right part: actions
        JPanel right =new JPanel();
        right.setBorder(new EmptyBorder(8,8,8,8));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setPreferredSize(new Dimension(260, 0));

        JPanel quick =new JPanel();
        quick.setLayout(new GridLayout(0,1,6,6));
        quick.setBorder(new TitledBorder("Quick Actions"));

        intervalBox =new JComboBox<>(new Integer[] {1,2,3,4,5,7,10,14,21});
        JPanel intervalRow =new JPanel(new BorderLayout(6,6));
        intervalRow.add(new JLabel("Interval (days):"), BorderLayout.NORTH);
        intervalRow.add(intervalBox, BorderLayout.CENTER);
        quick.add(intervalRow);

        JButton updateInterval = new JButton("Update Interval");
        JButton waterNow =new JButton("Water Now");
        JButton addBtn = new JButton("Add");
        JButton removeBtn =new JButton("Remove");
        JButton checkDue = new JButton("Check Due");

        quick.add(updateInterval);
        quick.add(waterNow);
        quick.add(addBtn);
        quick.add(removeBtn);
        quick.add(checkDue);

        right.add(quick);
        right.add(Box.createVerticalGlue());
        c.add(right,BorderLayout.EAST);

        //list selection listener
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Plant p = list.getSelectedValue();
                if (p == null) {
                    details.setText("Select a plant to see details");
                    notes.setText("");
                } else {
                    details.setText(p.detailString());
                    notes.setText(p.getNotes());
                    intervalBox.setSelectedItem(p.getIntervalDays());
                }
            }
        });

        // button actions
        saveNotesBtn.addActionListener(ae -> {
            Plant p = list.getSelectedValue();
            if (p != null) {
                p.setNotes(notes.getText().trim());
                JOptionPane.showMessageDialog(this, "Notes saved.");
            } else {
                JOptionPane.showMessageDialog(this, "Select a plant first.");
            }
        });

        addBtn.addActionListener(ae -> {
            JTextField nameF = new JTextField();
            JTextField speciesF = new JTextField();
            JComboBox<Integer> iv = new JComboBox<>(new Integer[] {1,2,3,4,5,7,10,14,21});
            Object[] fields = {
                "Name:", nameF,
                "Species:", speciesF,
                "Water interval (days):", iv
            };
            int r = JOptionPane.showConfirmDialog(this, fields, "Add Plant", JOptionPane.OK_CANCEL_OPTION);
            if (r == JOptionPane.OK_OPTION) {
                String name = nameF.getText().trim();
                String species = speciesF.getText().trim();
                Integer interval = (Integer) iv.getSelectedItem();
                if (name.isEmpty() || species.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Name and species required.");
                } else {
                    Plant np = new Plant(name, species, interval);
                    model.addElement(np);
                    list.setSelectedValue(np, true);
                }
            }
        });

        removeBtn.addActionListener(ae -> {
            Plant p = list.getSelectedValue();
            if (p != null) {
                int r = JOptionPane.showConfirmDialog(this, "Remove " + p.getName() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    model.removeElement(p);
                    details.setText("Select a plant to see details");
                    notes.setText("");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a plant to remove.");
            }
        });

        waterNow.addActionListener(ae -> {
            Plant p = list.getSelectedValue();
            if (p != null) {
                p.setLastWatered(new Date());
                details.setText(p.detailString());
                JOptionPane.showMessageDialog(this, p.getName() + " watered.");
            } else {
                JOptionPane.showMessageDialog(this, "Select a plant to water.");
            }
        });

        updateInterval.addActionListener(ae -> {
            Plant p = list.getSelectedValue();
            if (p != null) {
                Integer v = (Integer) intervalBox.getSelectedItem();
                p.setIntervalDays(v);
                details.setText(p.detailString());
                JOptionPane.showMessageDialog(this, "Interval updated.");
            } else {
                JOptionPane.showMessageDialog(this, "Select a plant first.");
            }
        });

        checkDue.addActionListener(ae -> {
            Date now = new Date();
            ArrayList<Plant> due = new ArrayList<>();
            for (int i = 0; i < model.size(); i++) {
                Plant p = model.get(i);
                Date lw = p.getLastWatered();
                if (lw == null) {
                    due.add(p);
                } else {
                    long diff = now.getTime() - lw.getTime();
                    long days = diff / (1000L * 60 * 60 * 24);
                    if (days >= p.getIntervalDays()) due.add(p);
                }
            }
            if (due.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No plants are due for watering.");
            } else {
                StringBuilder sb = new StringBuilder();
                for (Plant p : due) sb.append("- ").append(p.getName()).append(" (every ").append(p.getIntervalDays()).append(" days)\n");
                JOptionPane.showMessageDialog(this, "Plants due:\n" + sb.toString());
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PlantCareManagerSimple app = new PlantCareManagerSimple();
            app.setVisible(true);
        });
    }
}