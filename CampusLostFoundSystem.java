import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

public class CampusLostFoundSystem extends JFrame {
    private static final String[] COLUMNS = {"ID","Type","Item","Category","Location","Date","Contact","Status","Description"};
    private static final String DATA_FILE = "lost_found_records.csv";
    private final JTextField itemField = new JTextField(16);
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"Lost","Found"});
    private final JComboBox<String> categoryBox = new JComboBox<>(new String[]{"Electronics","ID Card","Books/Notes","Wallet/Purse","Keys","Clothing","Bottle/Accessories","Bag","Other"});
    private final JTextField locationField = new JTextField(16);
    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 10);
    private final JTextField contactField = new JTextField(16);
    private final JComboBox<String> statusBox = new JComboBox<>(new String[]{"Open","Claimed/Returned"});
    private final JTextArea descriptionArea = new JTextArea(3, 16);
    private final JTextField searchField = new JTextField(18);
    private final JComboBox<String> filterBox = new JComboBox<>(new String[]{"All Types","Lost","Found"});
    private final DefaultTableModel model = new DefaultTableModel(COLUMNS, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel countLabel = new JLabel();
    private final List<Record> records = new ArrayList<>();
    private int nextId = 1;
    private Integer selectedId = null;

    public CampusLostFoundSystem() {
        super("Campus Lost and Found Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);
        buildUI();
        loadRecords();
        refreshTable();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(12,12));
        root.setBorder(new EmptyBorder(16,16,16,16));
        root.setBackground(new Color(245,248,252));
        setContentPane(root);

        JLabel title = new JLabel("Campus Lost & Found");
        title.setFont(new Font("SansSerif",Font.BOLD,26));
        title.setForeground(new Color(30,55,92));
        JLabel sub = new JLabel("Report lost items, list found items, and track returns");
        sub.setForeground(new Color(90,103,123));
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading,BoxLayout.Y_AXIS));
        heading.add(title); heading.add(Box.createVerticalStrut(4)); heading.add(sub);
        root.add(heading,BorderLayout.NORTH);

        JPanel form = new JPanel(new BorderLayout(8,8));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220,226,235)), new EmptyBorder(12,12,12,12)));
        form.setPreferredSize(new Dimension(315,100));
        JLabel formTitle = new JLabel("Item Details");
        formTitle.setFont(new Font("SansSerif",Font.BOLD,17));
        form.add(formTitle,BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,2,4,2); g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;
        addField(fields,g,"Item name *",itemField);
        addField(fields,g,"Record type",typeBox);
        addField(fields,g,"Category",categoryBox);
        addField(fields,g,"Location *",locationField);
        addField(fields,g,"Date (YYYY-MM-DD) *",dateField);
        addField(fields,g,"Contact (optional)",contactField);
        addField(fields,g,"Status",statusBox);
        g.gridx=0; g.gridy++; fields.add(new JLabel("Description"),g);
        g.gridy++; g.weighty=1; g.fill=GridBagConstraints.BOTH;
        descriptionArea.setLineWrap(true); descriptionArea.setWrapStyleWord(true);
        fields.add(new JScrollPane(descriptionArea),g);
        form.add(fields,BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(2,2,6,6)); actions.setOpaque(false);
        JButton add = new JButton("Add Record"), update = new JButton("Update Selected");
        JButton clear = new JButton("Clear Form"), delete = new JButton("Delete Selected");
        add.addActionListener(e -> addRecord());
        update.addActionListener(e -> updateRecord());
        clear.addActionListener(e -> clearForm());
        delete.addActionListener(e -> deleteRecord());
        actions.add(add); actions.add(update); actions.add(clear); actions.add(delete);
        form.add(actions,BorderLayout.SOUTH);
        root.add(form,BorderLayout.WEST);

        JPanel listing = new JPanel(new BorderLayout(8,8)); listing.setOpaque(false);
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT,7,7));
        search.setBackground(Color.WHITE);
        search.setBorder(BorderFactory.createLineBorder(new Color(220,226,235)));
        search.add(new JLabel("Search:")); search.add(searchField); search.add(filterBox);
        JButton find = new JButton("Search"), showAll = new JButton("Show All");
        search.add(find); search.add(showAll);
        find.addActionListener(e -> refreshTable());
        searchField.addActionListener(e -> refreshTable());
        filterBox.addActionListener(e -> refreshTable());
        showAll.addActionListener(e -> { searchField.setText(""); filterBox.setSelectedIndex(0); refreshTable(); });
        listing.add(search,BorderLayout.NORTH);

        table.setRowHeight(25); table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true); table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.convertRowIndexToModel(table.getSelectedRow());
                try {
                    selectedId = Integer.parseInt(String.valueOf(model.getValueAt(row,0)));
                    fillForm(findById(selectedId));
                } catch (NumberFormatException ignored) { selectedId = null; }
            }
        });
        listing.add(new JScrollPane(table),BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout()); bottom.setOpaque(false);
        countLabel.setForeground(new Color(70,85,106)); bottom.add(countLabel,BorderLayout.WEST);
        JButton save = new JButton("Save Records Now");
        save.addActionListener(e -> { if (saveRecords()) JOptionPane.showMessageDialog(this,"Records saved to "+DATA_FILE); });
        bottom.add(save,BorderLayout.EAST); listing.add(bottom,BorderLayout.SOUTH);
        root.add(listing,BorderLayout.CENTER);

        JLabel note = new JLabel("Select a row to edit it. Use fictional contact details for demonstrations.");
        note.setForeground(new Color(90,103,123)); root.add(note,BorderLayout.SOUTH);
    }

    private void addField(JPanel p, GridBagConstraints g, String label, JComponent component) {
        g.gridx=0; g.gridy++; g.weighty=0; g.fill=GridBagConstraints.HORIZONTAL;
        p.add(new JLabel(label),g); g.gridy++; p.add(component,g);
    }

    private Record readForm() {
        String name=itemField.getText().trim(), location=locationField.getText().trim(), date=dateField.getText().trim();
        if(name.isEmpty() || location.isEmpty() || date.isEmpty()) { error("Please fill in Item name, Location, and Date."); return null; }
        try { LocalDate.parse(date); }
        catch(DateTimeParseException ex) { error("Enter a valid date in YYYY-MM-DD format, for example 2026-10-25."); return null; }
        Record r=new Record();
        r.type=String.valueOf(typeBox.getSelectedItem()); r.name=name;
        r.category=String.valueOf(categoryBox.getSelectedItem()); r.location=location; r.date=date;
        r.contact=contactField.getText().trim(); r.status=String.valueOf(statusBox.getSelectedItem());
        r.description=descriptionArea.getText().trim(); return r;
    }

    private void addRecord() {
        Record r=readForm(); if(r==null)return;
        r.id=nextId++; records.add(r);
        if(saveRecords()){ refreshTable(); clearForm(); JOptionPane.showMessageDialog(this,"Record added successfully."); }
        else { records.remove(r); nextId--; }
    }

    private void updateRecord() {
        if(selectedId==null){error("Select a record from the table before updating.");return;}
        Record old=findById(selectedId); if(old==null)return;
        Record r=readForm(); if(r==null)return;
        r.id=old.id; int index=records.indexOf(old); records.set(index,r);
        if(saveRecords()){refreshTable();clearForm();JOptionPane.showMessageDialog(this,"Record updated successfully.");}
        else records.set(index,old);
    }

    private void deleteRecord() {
        if(selectedId==null){error("Select a record from the table before deleting.");return;}
        Record r=findById(selectedId); if(r==null)return;
        int answer=JOptionPane.showConfirmDialog(this,"Delete the record for '"+r.name+"'?","Confirm Delete",JOptionPane.YES_NO_OPTION);
        if(answer!=JOptionPane.YES_OPTION)return;
        int index=records.indexOf(r); records.remove(r);
        if(saveRecords()){refreshTable();clearForm();} else records.add(index,r);
    }

    private void fillForm(Record r) {
        if(r==null)return;
        itemField.setText(r.name); typeBox.setSelectedItem(r.type); categoryBox.setSelectedItem(r.category);
        locationField.setText(r.location); dateField.setText(r.date); contactField.setText(r.contact);
        statusBox.setSelectedItem(r.status); descriptionArea.setText(r.description);
    }

    private void clearForm() {
        selectedId=null; table.clearSelection(); itemField.setText(""); typeBox.setSelectedIndex(0);
        categoryBox.setSelectedIndex(0); locationField.setText(""); dateField.setText(LocalDate.now().toString());
        contactField.setText(""); statusBox.setSelectedIndex(0); descriptionArea.setText("");
    }

    private void refreshTable() {
        String query=searchField.getText().trim().toLowerCase(Locale.ROOT);
        String type=String.valueOf(filterBox.getSelectedItem()); model.setRowCount(0);
        for(Record r:records) {
            if(!"All Types".equals(type) && !r.type.equals(type)) continue;
            String searchable=(r.name+" "+r.category+" "+r.location+" "+r.date+" "+r.contact+" "+r.status+" "+r.description).toLowerCase(Locale.ROOT);
            if(!query.isEmpty() && !searchable.contains(query)) continue;
            model.addRow(new Object[]{r.id,r.type,r.name,r.category,r.location,r.date,r.contact,r.status,r.description});
        }
        countLabel.setText(model.getRowCount()+" matching record(s) | "+records.size()+" total");
    }

    private Record findById(int id) { for(Record r:records) if(r.id==id)return r; return null; }

    private void loadRecords() {
        Path path=Paths.get(DATA_FILE);
        if(!Files.exists(path)) {
            records.add(new Record(nextId++,"Lost","Blue Water Bottle","Bottle/Accessories","CSE Block - Room 204",LocalDate.now().toString(),"student@example.com","Open","Fictional sample record."));
            records.add(new Record(nextId++,"Found","USB Drive","Electronics","Library Reading Hall",LocalDate.now().toString(),"student@example.com","Open","Small black USB drive found near a computer."));
            records.add(new Record(nextId++,"Lost","Student ID Card","ID Card","Main Canteen",LocalDate.now().toString(),"student@example.com","Open","Fictional sample record."));
            saveRecords(); return;
        }
        try(BufferedReader br=Files.newBufferedReader(path,StandardCharsets.UTF_8)) {
            String line; boolean first=true;
            while((line=br.readLine())!=null) {
                if(first){first=false;if(line.startsWith("id,"))continue;}
                List<String> v=parseCsv(line); if(v.size()!=9)continue;
                try {
                    Record r=new Record(); r.id=Integer.parseInt(v.get(0)); r.type=v.get(1); r.name=v.get(2);
                    r.category=v.get(3); r.location=v.get(4); r.date=v.get(5); r.contact=v.get(6);
                    r.status=v.get(7); r.description=v.get(8); records.add(r); nextId=Math.max(nextId,r.id+1);
                } catch(RuntimeException ignored) {}
            }
        } catch(IOException ex) { error("Could not read saved records: "+ex.getMessage()); }
    }

    private boolean saveRecords() {
        try(BufferedWriter bw=Files.newBufferedWriter(Paths.get(DATA_FILE),StandardCharsets.UTF_8)) {
            bw.write("id,type,name,category,location,date,contact,status,description"); bw.newLine();
            for(Record r:records) {
                bw.write(csv(r.id)+","+csv(r.type)+","+csv(r.name)+","+csv(r.category)+","+csv(r.location)+","+csv(r.date)+","+csv(r.contact)+","+csv(r.status)+","+csv(r.description));
                bw.newLine();
            }
            return true;
        } catch(IOException ex) { error("Could not save records: "+ex.getMessage()); return false; }
    }

    private String csv(Object value) { return "\""+String.valueOf(value==null?"":value).replace("\"","\"\"")+"\""; }

    private List<String> parseCsv(String line) {
        List<String> out=new ArrayList<>(); StringBuilder cell=new StringBuilder(); boolean quoted=false;
        for(int i=0;i<line.length();i++) {
            char c=line.charAt(i);
            if(c=='"') {
                if(quoted && i+1<line.length() && line.charAt(i+1)=='"'){cell.append('"');i++;}
                else quoted=!quoted;
            } else if(c==',' && !quoted){out.add(cell.toString());cell.setLength(0);}
            else cell.append(c);
        }
        out.add(cell.toString()); return out;
    }

    private void error(String message) { JOptionPane.showMessageDialog(this,message,"Please Check",JOptionPane.ERROR_MESSAGE); }

    private static class Record {
        int id; String type,name,category,location,date,contact,status,description;
        Record() {}
        Record(int id,String type,String name,String category,String location,String date,String contact,String status,String description) {
            this.id=id;this.type=type;this.name=name;this.category=category;this.location=location;
            this.date=date;this.contact=contact;this.status=status;this.description=description;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch(Exception ignored) {}
            new CampusLostFoundSystem().setVisible(true);
        });
    }
}
