package com.pi.code.tool.flowchart;

import java.awt.Color;
import java.util.Stack;

import com.pi.code.tool.flowchart.DrawingPanel.ChartComponent;

public class UndoManager {
	public static interface UndoCallback {
		void setEnabled(boolean undoEnabled, boolean redoEnabled);

		void undo(UndoAction action);
		
		void redo(UndoAction action);
	}

	public static class UndoAction {
		public ChartComponent comp;
		public boolean addNew;
		UndoAction(ChartComponent comp, boolean addNew) {
		   this.comp = comp;
		   this.addNew = addNew;
		}
		 
	}

	public UndoManager() {
	}

	UndoCallback undoCallback;
	Stack<UndoAction> undolist = new Stack<UndoAction>();
	Stack<UndoAction> redolist = new Stack<UndoAction>();
	
	public void setUndoCallback(UndoCallback undoCallback) {
		this.undoCallback = undoCallback;
	}

	public void pushAdd( ChartComponent comp ) {
		UndoAction action = new UndoAction(comp, true);
		undolist.add(action);
		redolist.clear();
		if (undoCallback != null)
			undoCallback.setEnabled(true, false);
	}
	
	public void pushDelete( ChartComponent comp ) {
		UndoAction action = new UndoAction(comp, false);
		undolist.add(action);
		redolist.clear();
		if (undoCallback != null)
			undoCallback.setEnabled(true, false);
	}

	public void clear() {
		undolist.clear();
		redolist.clear();
		if (undoCallback != null)
			undoCallback.setEnabled(false, false);
	}

	public void undo() {
		if (undolist.size() == 0)
			return;
		try {
			UndoAction action = undolist.pop();
			redolist.push(action);
			if (undoCallback != null) {
				undoCallback.undo(action);
				undoCallback.setEnabled(!undolist.isEmpty(), true);
			}
		} catch (Exception ex) {
		}
	}
	
	public void redo() {
		if (redolist.size() == 0)
			return;
		try {
			UndoAction action = redolist.pop(); 
			undolist.push(action);
			if (undoCallback != null) {
				undoCallback.redo(action);
				undoCallback.setEnabled(true, !redolist.isEmpty());
			}
		} catch (Exception ex) {
		}
	}

}
