package com.pi.code.tool.debug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.pi.code.tool.codefillin.PropertyUIHelper;
import com.pi.code.tool.debug.DataProcessor.Debug;
import com.pi.code.tool.debug.DataProcessor.DebugLine;
import com.pi.code.tool.util.CodePlayerFrame;

public class WorkbenchForDebug extends CodePlayerFrame {
 
	private static final long serialVersionUID = 1L;

	public static interface DataProvider {
		String getCode();
		Debug getDebug();

		void setResult(Debug debug);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WorkbenchForDebug workbench = new WorkbenchForDebug(null);
		workbench.setVisible(true);
	}

	private DataProvider provider;

	private JButton backToCodePlayerButton;

	private Debug debug ;
	private List<String> varShortNames = new ArrayList<>();

	private DebugTableModel debugTableModel;

	private JTable debugTable;

	private JScrollPane debugListScrollPane;

	private JButton addNewColumnButton;

	private JButton addNewRowButton;

	private JButton deleteSelectedRowButton;

	private JButton insertNewRowButton;

	private JButton deleteColumnButton;

	private JTextArea workspace;

	private JButton addNewSuggestionRowButton;

	private JTextArea rowindex;

	private int maxRowCount = 0;
	public WorkbenchForDebug(final DataProvider provider) {
		this.provider = provider;
		setLayout(new BorderLayout());

	   workspace = new JTextArea(200,50); 
	   
	   rowindex = new JTextArea(200,1);
	   rowindex.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
	   
	   JPanel workspacewrap = new JPanel();
	   workspacewrap.setLayout(new BorderLayout());
	   workspacewrap.add(rowindex, BorderLayout.WEST);
	   workspacewrap.add(workspace, BorderLayout.CENTER);
		   
		 
	   JScrollPane scrollPane = new JScrollPane(workspacewrap);
       scrollPane .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
       scrollPane .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);//.VERTICAL_SCROLLBAR_AS_NEEDED);

       //scrollPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
       scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK),
						BorderFactory.createEmptyBorder(4, 4, 4, 4) )));
		if( provider != null) {
			String code = provider.getCode();
			String[] lines = code.split("\n");
			this.maxRowCount = lines.length;
			 
			workspace.setText(code);
		}
		maxRowCount = Math.max(200, maxRowCount);
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < maxRowCount; i++)
			sb.append(i + "\n");
		rowindex.setText(sb.toString());
		
		add(scrollPane, BorderLayout.CENTER);
		
		debugTableModel = new DebugTableModel();
		debugTable = new JTable(debugTableModel) {
			public Class getColumnClass(int column) {
				if( column == varShortNames.size() + 2)
					return Boolean.class;
				return String.class;
			}
		};
		debugTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//       ListSelectionModel cellSelectionModel = debugTable.getSelectionModel();
//       cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//
//       cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
//         public void valueChanged(ListSelectionEvent e) {
//           String selectedData = null; 
//           int[] selectedRow = debugTable.getSelectedRows();
//           if( selectedRow.length == 1) { 
//           }
//         } 
//       }); 
		debugListScrollPane = new JScrollPane(debugTable);
		debugTable.setFillsViewportHeight(true);
		debugTable.setDefaultEditor(String.class, new JTextFieldEditor());
		debugTable.setDefaultEditor(Boolean.class, new BooleanEditor()); 
		
		debugTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer());
		debugListScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		debugListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		debugListScrollPane.setPreferredSize(new Dimension(350, 680));

		debugListScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK),
						BorderFactory.createEmptyBorder(4, 4, 4, 4) )));
		 
		
		add(debugListScrollPane, BorderLayout.EAST);

		addNewColumnButton = new JButton("加新变量、列");
		addNewColumnButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String v = JOptionPane.showInputDialog("变量名");
				if (v == null || v.length() == 0)
					return;
				if (debug == null)
					return;
				if (debug.vars.keySet().contains(v))
					return;
				String next = "a" ;
				if( varShortNames.size() > 0) {
					char c = varShortNames.get(varShortNames.size()-1).charAt(0);
					c = (char) (c+1);
					next = Character.toString(c);
				}
				varShortNames.add(next);
				debug.vars.put(v, next);
				debugTableModel.rebuildNewStructure();
			}
		});
		deleteColumnButton = new JButton("删除变量、列");
		deleteColumnButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (debug == null)
					return;
				JPopupMenu menu = new JPopupMenu();
				for (final String v : debug.vars.keySet()) {
					JMenuItem item = new JMenuItem(v);
					item.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							String n = debug.vars.get(v);
							varShortNames.remove(n);
							debug.deleteVariable(v);
							debugTableModel.rebuildNewStructure();
						}
					});
					menu.add(item);
				}
				menu.show(deleteColumnButton, 10, 10);
			}
		});
		addNewRowButton = new JButton("添加新执行行");
		addNewRowButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (debug == null)
					return;
				DebugLine line = new DebugLine();
				debug.lines.add(line);
				if (debug.lines.size() > 1) {
					line.r = debug.lines.get(debug.lines.size() - 2).r + 1;
				}
				debugTableModel.fireTableRowsInserted(debug.lines.size() - 1, debug.lines.size() - 1);
				 SwingUtilities.invokeLater(new Runnable() {
				        @Override
				        public void run() {
				        	debugTable.scrollRectToVisible(debugTable.getCellRect(debug.lines.size() - 1, 0, false));
				        }
				    });
			}
		});
		 
		insertNewRowButton = new JButton("插入新执行行");
		insertNewRowButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (debug == null)
					return;
				int[] selectedRow = debugTable.getSelectedRows();
				int pos = selectedRow == null || selectedRow.length == 0 ? debug.lines.size() - 1 : selectedRow[0];

				DebugLine line = new DebugLine();
				debug.lines.add(pos, line);

				debugTableModel.rebuildList();
			}
		});

		deleteSelectedRowButton = new JButton("删除选择行");
		deleteSelectedRowButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (debug == null)
					return;
				int[] selectedRow = debugTable.getSelectedRows();
				if (selectedRow == null || selectedRow.length == 0)
					return;

				debug.lines.remove(selectedRow[0]);

				debugTableModel.rebuildList();
			}
		});

		backToCodePlayerButton = new JButton("返回CodePlayer");
		backToCodePlayerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (provider == null)
					return;
				Debug data = fromUI();
				provider.setResult(data);
				setVisible(false);
			}
		});

		JPanel bottomRow = new JPanel();
		bottomRow.setLayout(new BoxLayout(bottomRow, BoxLayout.Y_AXIS));
		bottomRow.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		bottomRow.add(PropertyUIHelper.createRow("修改观察的变量", addNewColumnButton, deleteColumnButton));
		bottomRow.add(PropertyUIHelper.createRow("修改观察点", addNewRowButton, insertNewRowButton,
				deleteSelectedRowButton));
		bottomRow.add(PropertyUIHelper.createLine());
		bottomRow.add(PropertyUIHelper.createRow("保存到CodePlayer:  ", backToCodePlayerButton));

		add(bottomRow, BorderLayout.SOUTH);

		if (provider != null) {
			buildUIFromData(provider.getDebug());
		} else {
			buildUIFromData(null);
		}
		setSize(900, 600);
		setVisible(true);
	}

	protected void buildUIFromData(Debug debug) {
		this.debug = debug;
		this.varShortNames = new ArrayList<>();
		if( this.debug == null)
			this.debug = new Debug();
		else {
			for(String n : this.debug.vars.values()) {
				if( !"out".equals(n) && !"msg".equals(n))
				    this.varShortNames.add(n);
			}
			Collections.sort(this.varShortNames);
//			Collections.sort(this.varShortNames, new Comparator<String>() {
//
//				@Override
//				public int compare(String o1, String o2) {
//					if( o1.equals(o2)) return 0;
//					return - o1.compareTo(o2);
//				}});
		}
		debugTableModel.rebuildNewStructure();
	}

	public Debug fromUI() {
		debug.cleanup();
		return debug;
	}

	public class DebugTableModel extends AbstractTableModel {

		public DebugTableModel() {
		}

		public String getColumnName(int col) {
			if (varShortNames == null || varShortNames.isEmpty())
				return "";
			if (col == 0)
				return "行号";
			if( col == varShortNames.size() + 1)
				  return "msg";
			if( col == varShortNames.size() + 2)
				  return "msg/out?";
			String sn = varShortNames.get(col - 1);
			for(Entry<String, String> s : debug.vars.entrySet()) {
				if( s.getValue().equals(sn))
					return s.getKey();
			}
			return "";
		}

		public int getRowCount() {
			if (varShortNames == null || varShortNames.isEmpty())
				 return 0;
			return debug.lines.size();
		}

		public int getColumnCount() {
			return 1 +  varShortNames.size() + 2;
		}

		public void rebuildList() {
			this.fireTableDataChanged();
		}

		public void rebuildNewStructure() {
			this.fireTableStructureChanged();
		}

		public String getVariByColumnIndex(int col) {
			if (varShortNames == null || varShortNames.isEmpty())
				return "";
			if( col == varShortNames.size() + 1)
				  return "msg";
			if( varShortNames.size() >= col)
			  return varShortNames.get(col - 1);
			
			return "";
		}

		public Object getValueAt(int row, int col) {
			if (varShortNames == null || varShortNames.isEmpty())
				return "";
			DebugLine line = debug.lines.get(row);
			if (col == 0)
				return line.r + "";
			if( col == this.getColumnCount()-1) {
				return line.getValue2("msg").length() > 0;
			}
			String variable = getVariByColumnIndex(col);
			return line.getValue(variable);
		}

		public boolean isCellEditable(int row, int col) {
			return true;
		}

		public void setValueAt(Object value, int row, int col) {
			if (varShortNames == null || varShortNames.isEmpty() || value == null)
				return  ;
			DebugLine line = debug.lines.get(row);
			if (col == 0) {
				int v = Integer.parseInt((String) value);
				line.r = v;
			} else {
				if( col == this.getColumnCount()-1) {
					boolean ismsg = (Boolean)value;
					String v = line.getValue("msg");
					line.setValue(ismsg? "msg":"out", v);
				}
				else if( col == this.getColumnCount()-2) { 
					String v = line.getMsgTagUsed( );
					if( v.length() == 0) 
						v = "msg";
					line.setValue(v, (String)value);
				}
				else {
					String variable = getVariByColumnIndex(col);
					line.setValue(variable, (String) value);
				} 
			}
			this.fireTableRowsUpdated(row, row);
		}
	};

	public class JTextFieldEditor extends DefaultCellEditor {
		JTextField tmpjtf;

		public JTextFieldEditor() {
			super(new JTextField());
			tmpjtf = (JTextField) super.editorComponent;
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			tmpjtf.setText("");
			return tmpjtf;
		}

		public Object getCellEditorValue() {
			return new String(tmpjtf.getText());
		}

		public boolean stopCellEditing() {
			return super.stopCellEditing();
		}

		public void fireEditingStopped() {
			super.fireEditingStopped();
		}
	}
	
	public class BooleanEditor extends DefaultCellEditor {
		JCheckBox tmpjtf;

		public BooleanEditor() {
			super(new JCheckBox());
			tmpjtf = (JCheckBox) super.editorComponent;
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			tmpjtf.setSelected( (Boolean)value);
			return tmpjtf;
		}

		public Object getCellEditorValue() {
			return tmpjtf.isSelected();
		}

		public boolean stopCellEditing() {
			return super.stopCellEditing();
		}

		public void fireEditingStopped() {
			super.fireEditingStopped();
		}
	}
}
