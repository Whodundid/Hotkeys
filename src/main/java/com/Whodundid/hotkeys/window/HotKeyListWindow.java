package com.Whodundid.hotkeys.window;

import com.Whodundid.core.EnhancedMC;
import com.Whodundid.core.app.AppType;
import com.Whodundid.core.app.EMCApp;
import com.Whodundid.core.app.RegisteredApps;
import com.Whodundid.core.debug.IDebugCommand;
import com.Whodundid.core.util.EUtil;
import com.Whodundid.core.util.renderUtil.CenterType;
import com.Whodundid.core.util.renderUtil.EColors;
import com.Whodundid.core.windowLibrary.windowObjects.actionObjects.WindowButton;
import com.Whodundid.core.windowLibrary.windowObjects.advancedObjects.textArea.TextAreaLine;
import com.Whodundid.core.windowLibrary.windowObjects.advancedObjects.textArea.WindowTextArea;
import com.Whodundid.core.windowLibrary.windowObjects.basicObjects.WindowLabel;
import com.Whodundid.core.windowLibrary.windowObjects.windows.WindowDialogueBox;
import com.Whodundid.core.windowLibrary.windowObjects.windows.WindowSelectionList;
import com.Whodundid.core.windowLibrary.windowObjects.windows.WindowDialogueBox.DialogueBoxTypes;
import com.Whodundid.core.windowLibrary.windowTypes.WindowParent;
import com.Whodundid.core.windowLibrary.windowTypes.interfaces.IActionObject;
import com.Whodundid.hotkeys.HotKeyApp;
import com.Whodundid.hotkeys.control.HotKey;
import com.Whodundid.hotkeys.control.KeyActionType;
import com.Whodundid.hotkeys.control.hotKeyTypes.CommandSenderHotKey;
import com.Whodundid.hotkeys.control.hotKeyTypes.DebugHotKey;
import com.Whodundid.hotkeys.control.hotKeyTypes.GuiOpenerHotKey;
import com.Whodundid.hotkeys.control.hotKeyTypes.ConditionalCommandSenderHotKey;
import com.Whodundid.hotkeys.control.hotKeyTypes.ModActivatorHotKey;
import com.Whodundid.hotkeys.control.hotKeyTypes.ModDeactivatorHotKey;
import com.Whodundid.hotkeys.util.HKResources;
import net.minecraft.util.EnumChatFormatting;

//Last edited: Jan 7, 2019
//First Added: Jan 7, 2019
//Author: Hunter Bragg

public class HotKeyListWindow extends WindowParent {
	
	HotKeyApp mod = (HotKeyApp) RegisteredApps.getApp(AppType.HOTKEYS);
	WindowTextArea keyList;
	WindowButton edit, sortList, delete, toggleEnabled;
	WindowDialogueBox msgBox;
	WindowSelectionList sortSelection;
	EMCApp selectedMod;
	HotKey currentKey;
	boolean hasCategory = false, hasArg1 = false, hasArg2 = false, hasDescription = false;
	String keyName = "";
	String keyType = "";
	String keys = "";
	String keyCategory = "";
	String keyArg1String = "", keyArg1 = "";
	String keyArg2String = "", keyArg2 = "";
	WindowLabel desc;
	int listVerticalPos = 0;
	
	public HotKeyListWindow() { this(null); }
	public HotKeyListWindow(EMCApp modIn) {
		super();
		selectedMod = modIn;
		aliases.add("hotkeylist", "keylist", "hklist");
		windowIcon = HKResources.iconCreator;
	}
	
	@Override
	public void initWindow() {
		setObjectName("Hotkey List");
		setDimensions(450, 275);
	}
	
	@Override
	public void initObjects() {
		defaultHeader(this);
		
		keyList = new WindowTextArea(this, endX - 198, startY + 20, 190, 220).setDrawLineNumbers(true);
		
		edit = new WindowButton(this, startX + 9, endY - 28, 100, 20, "Edit Key");
		delete = new WindowButton(this, keyList.startX - 7 - 100, endY - 28, 100, 20, "Delete Key");
		toggleEnabled = new WindowButton(this, keyList.midX - (130 / 2), endY - 28, 130, 20, "Enabled");
		//sortList = new EGuiButton(this, endX - 178, endY - 28, 150, 20, "Sort list by..");
		desc = new WindowLabel(this, startX + 28, startY + 205, "").enableWordWrap(true, 208);
		
		toggleEnabled.setEnabled(false);
		edit.setEnabled(false);
		delete.setEnabled(false);
		//sortList.setEnabled(false);
		
		edit.setStringColor(EColors.yellow);
		delete.setStringColor(EColors.lred);
		
		addObject(null, edit, delete, toggleEnabled, keyList, desc);
		
		buildKeyList(mod.getDefaultListSort());
	}
	
	@Override
	public void drawObject(int mXIn, int mYIn) {
		drawDefaultBackground();
		
		drawStringCS("Registered Hotkeys", endX - keyList.width / 2 - 8, startY + 7, 0xb2b2b2);
		drawStringCS("Selected HotKey's Values", startX + 126, startY + 7, 0xb2b2b2);
		
		//draw hotkey value display container
		drawRect(startX + 9, startY + 20, startX + 245, endY - 35, 0xff000000);
		drawRect(startX + 10, startY + 21, startX + 244, endY - 36, 0xff2D2D2D);
		
		//draw separator lines
		if (keyList.getCurrentLine() != null && keyList.getCurrentLine().getStoredObj() != null) {
			HotKey k = (HotKey) keyList.getCurrentLine().getStoredObj();
			loadKeyValues(k);
			drawRect(startX + 10, startY + 48, startX + 244, startY + 49, 0xff000000);
			drawRect(startX + 10, startY + 76, startX + 244, startY + 77, 0xff000000);
			drawRect(startX + 10, startY + 104, startX + 244, startY + 105, 0xff000000);
			drawRect(startX + 10, startY + 132, startX + 244, startY + 133, 0xff000000);
			if (hasArg2) { drawRect(startX + 10, startY + 160, startX + 244, startY + 161, 0xff000000); }
			drawRect(startX + 10, startY + 188, startX + 244, startY + 189, 0xff000000);
		}
		else { resetValues(); }
		
		drawKeyValues();
		
		super.drawObject(mXIn, mYIn);
	}
	
	@Override
	public void actionPerformed(IActionObject object, Object... args) {
		if (object.equals(edit)) {
			if (keyList.getCurrentLine() != null && keyList.getCurrentLine().getStoredObj() != null) {
				HotKey key = (HotKey) keyList.getCurrentLine().getStoredObj();
				EnhancedMC.displayWindow(new HotKeyCreatorWindow(key), this);
			}
		}
		if (object.equals(delete)) {
			if (keyList.getCurrentLine() != null && keyList.getCurrentLine().getStoredObj() != null) {
				deleteKey((HotKey) keyList.getCurrentLine().getStoredObj());
			}
		}
		if (object.equals(toggleEnabled)) {
			if (keyList.getCurrentLine() != null && keyList.getCurrentLine().getStoredObj() != null) {
				HotKey k = (HotKey) keyList.getCurrentLine().getStoredObj();
				toggleEnabled(k);
			}
		}
		if (object.equals(sortList)) { openSortSelectionList(); }
		if (object.equals(sortSelection)) { if (sortSelection.getSelectedObject() instanceof String) { buildKeyList((String) sortSelection.getSelectedObject()); } }
	}
	
	protected void drawKeyValues() {
		if (!keyName.isEmpty()) { drawStringS("Name:", startX + 14, startY + 24, 0xffbb00); }
		else {
			drawStringCS("Click on a hotkey from the registered", startX + 126, startY + 120, 0xffbb00);
			drawStringCS("hotkeys list to see its values.", startX + 126, startY + 132, 0xffbb00);
		}
		if (!keyType.isEmpty()) {
			drawStringS("Type:", startX + 14, startY + 52, 0xffbb00);
			drawStringS("Keys:", startX + 14, startY + 80, 0xffbb00);
			drawStringS("Category:", startX + 14, startY + 108, 0xffbb00);
			if (!keyArg1String.isEmpty()) { drawStringS(keyArg1String, startX + 14, startY + 136, 0xffbb00); }
			else { drawStringS("", startX + 14, startY + 136, 0xb2b2b2); }
			if (!keyArg2String.isEmpty()) { drawStringS(keyArg2String, startX + 14, startY + 164, 0xffbb00); }
			else { drawStringS("", startX + 14, startY + 164, 0xb2b2b2); }
			drawStringS("Description:", startX + 14, startY + 192, 0xffbb00);
			
			drawStringS(keyName, startX + 28, startY + 37, 0x00ffdc);
			drawStringS(keyType, startX + 28, startY + 65, 0x00ffdc);
			if (!keys.isEmpty()) { drawStringS(keys, startX + 28, startY + 93, 0x00ffdc); }
			else { drawStringS("No keys set", startX + 28, startY + 93, 0xb2b2b2); }
			if (hasCategory) { drawStringS(keyCategory, startX + 28, startY + 121, 0x00ffdc); }
			else { drawStringS("No category", startX + 28, startY + 121, 0xb2b2b2); }
			if (hasArg1) { drawStringS(keyArg1, startX + 28, startY + 149, 0x00ffdc); }
			else { drawStringS("", startX + 28, startY + 149, 0xb2b2b2); }
			if (hasArg2) { drawStringS(keyArg2, startX + 28, startY + 177, 0x00ffdc); }
			else { drawStringS("", startX + 28, startY + 177, 0xb2b2b2); }
			if (!hasDescription) { drawStringS("No description set", startX + 28, startY + 205, 0xb2b2b2); }
		}
	}
	
	public void loadAppKeys(EMCApp modIn) {
		
	}
	
	public void loadKeyValues(HotKey keyIn) {
		try {
			resetValues();
			if (keyIn != null) {
				edit.setEnabled(true);
				delete.setEnabled(true);
				
				hasCategory = false; hasArg1 = false; hasArg2 = false; hasDescription = false;
				KeyActionType type = keyIn.getHotKeyType();
				
				keyName = keyIn.getKeyName();
				keyType = KeyActionType.getStringFromType(keyIn.getHotKeyType());
				
				if (keyIn.getKeyCombo() != null && keyIn.getKeyCombo().getKeys() != null) {
					keys = EUtil.keysToString(keyIn.getKeyCombo().getKeys());
				}
				
				switch (type) {
				case APP: break;
				case COMMANDSENDER:
					hasArg1 = true;
					keyArg1String = "Command:";
					keyArg1 = ((CommandSenderHotKey) keyIn).getCommand();
					break;
				case CONDITIONAL_COMMAND_ITEMTEST:
					ConditionalCommandSenderHotKey k = (ConditionalCommandSenderHotKey) keyIn;
					hasArg1 = true;
					keyArg1String = "Command:";
					keyArg1 = k.getCommand();
					hasArg2 = true;
					keyArg2String = "Item id";
					keyArg2 = k.getItemID() + "";
					break;
				case DEBUG:
					hasArg1 = true;
					keyArg1String = "Debug Command:";
					keyArg1 = IDebugCommand.getDebugCommandName(((DebugHotKey) keyIn).getDebugFunction());
					break;
				case GUI_OPENER:
					hasArg1 = true;
					keyArg1String = "Gui to be opened:";
					keyArg1 = ((GuiOpenerHotKey) keyIn).getGuiDisplayName();
					break;
				case MC_KEYBIND_MODIFIER: break; //i don't know what i am doing with this yet
				case APP_ACTIVATOR:
					hasArg1 = true;
					keyArg1String = "SubMod to be activated:";
					keyArg1 = AppType.getAppName(((ModActivatorHotKey) keyIn).getApp());
					break;
				case APP_DEACTIVATOR:
					hasArg1 = true;
					keyArg1String = "SubMod to be deactivated:";
					keyArg1 = AppType.getAppName(((ModDeactivatorHotKey) keyIn).getApp());
					break;
				//case SCRIPT:
				//	hasArg1 = true;
				//	keyArg1String = "Script to be run:";
				//	keyArg1 = ((ScriptHotKey) keyIn).getScript().getScriptName();
				//	hasArg2 = true;
				//	keyArg2String = "Script arguments:";
				//	keyArg2 = ((ScriptHotKey) keyIn).getScriptArgs() + "";
				//	break;
				case UNDEFINED: break;
				default: break;
				}
				
				if (keyIn.getKeyCategory() != null) {
					String catName = keyIn.getKeyCategory().getCategoryName();
					if (catName != null && !catName.equals("null") && !catName.equals("none")) {
						hasCategory = true;
						keyCategory = keyIn.getKeyCategory().getCategoryName();
					}
				}
				
				if (!keyIn.getKeyDescription().isEmpty() && !keyIn.getKeyDescription().equals("No description set.")) {
					hasDescription = true;
					desc.setString(keyIn.getKeyDescription()).setColor(0x00ffdc);
				}
				
				toggleEnabled.setEnabled(true);
				toggleEnabled.setString(keyIn.isEnabled() ? "Enabled" : "Disabled");
				toggleEnabled.setStringColor(keyIn.isEnabled() ? 0x55ff55 : 0xff5555);
			}
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public void resetValues() {
		edit.setEnabled(false);
		delete.setEnabled(false);
		keyName = "";
		keyType = "";
		keys = "";
		keyCategory = "";
		keyArg1String = "";
		keyArg2String = "";
		desc.setString("");
		toggleEnabled.setEnabled(false);
		toggleEnabled.setString("No Key Selected");
		toggleEnabled.setStringColor(EColors.lgray);
		//toggleEnabled.setStringColor(EGuiButton.defaultColor);
	}
	
	private void toggleEnabled(HotKey key) {
		key.setEnabled(!key.isEnabled());
		resetValues();
		buildKeyList(mod.getDefaultListSort());
		TextAreaLine l = keyList.getLineWithObject(key);
		if (l != null) { keyList.setSelectedLine(l); }
		mod.saveHotKeys();
	}
	
	private void deleteKey(HotKey key) {
		if (key.isAppKey()) {
			msgBox = new WindowDialogueBox(DialogueBoxTypes.ok);
			msgBox.setTitle("HotKey Deletion Error");
			msgBox.setTitleColor(EColors.lgray.intVal);
			msgBox.setMessage("Cannot delete an EMC App's hotkey.").setMessageColor(0xff5555);
			EnhancedMC.displayWindow(msgBox);
			getTopParent().setFocusLockObject(msgBox);
		}
		else {
			msgBox = new WindowDialogueBox(DialogueBoxTypes.yesNo) {
				@Override
				public void actionPerformed(IActionObject object, Object... args) {
					if (object == yes) {
						close();
						if (mod.unregisterHotKey(key)) {
							mod.saveHotKeys();
							buildKeyList(mod.getDefaultListSort());
							//msgBox = new EGuiDialogueBox(guiInstance, midX - 125, midY - 48, 250, 75, DialogueBoxTypes.ok);
							//msgBox.setDisplayString("HotKey Deletion");
							//msgBox.setMessage("Sucessfully deleted hotkey: " + key.getKeyName() + ".").setMessageColor(0x55ff55);
							//guiInstance.addObject(msgBox);
						} else {
							msgBox = new WindowDialogueBox(DialogueBoxTypes.ok);
							msgBox.setTitle("HotKey Deletion");
							msgBox.setTitleColor(EColors.lgray.intVal);
							msgBox.setMessage("Failed to delete hotkey: " + key.getKeyName() + "!").setMessageColor(0xff5555);
							EnhancedMC.displayWindow(msgBox);
							getTopParent().setFocusLockObject(msgBox);
						}
					}
					if (object == no) { close(); }
				}
			};
			msgBox.setTitle("Hotkey Deletion");
			msgBox.setTitleColor(EColors.lgray.intVal);
			msgBox.setMessage("Are you sure you want to delete hotkey: " + key.getKeyName() + "?").setMessageColor(0xff5555);
			EnhancedMC.displayWindow(msgBox, CenterType.screen);
			getTopParent().setFocusLockObject(msgBox);
		}
	}
	
	private void openSortSelectionList() {
		sortSelection = new WindowSelectionList(this);
		sortSelection.addOption("Is Enabled", "enabled");
		sortSelection.addOption("By App", "app");
		sortSelection.addOption("By Category", "category");
		sortSelection.addOption("Alphabetically (A-Z)", "nameup");
		sortSelection.addOption("Alphabetically (Z-A)", "namedown");
		EnhancedMC.displayWindow(sortSelection, this, true, false, false, CenterType.object);
	}
	
	protected void buildKeyList(String sortType) {
		keyList.clear();
		
		if (mod.getRegisteredHotKeys().isEmpty()) {
			keyList.addTextLine("No Hotkeys", 0xb2b2b2);
		}
		else {
			switch (sortType) {
			case "enabled": sortListByEnabled(); break;
			case "app": sortListByApp(); break;
			case "category": sortListByCategory(); break;
			case "nameup": sortListByNameUp(); break;
			case "namedown": sortListByNameDown(); break;
			default: System.out.println("!! Unrecognized sort type !!"); break;
			}
		}
	}
	
	private void sortListByEnabled() {
		boolean anyDisabled = false, anyEnabled = false;
		for (HotKey k : mod.getRegisteredHotKeys()) { if (!k.isEnabled()) { anyDisabled = true; break; } }
		for (HotKey k : mod.getRegisteredHotKeys()) { if (k.isEnabled()) { anyEnabled = true; break; } }
		if (anyEnabled) { keyList.addTextLine("Enabled Hotkeys:").setLineNumberColor(0xb2b2b2).setTextColor(0x00ffdc); }
		for (HotKey k : mod.getRegisteredHotKeys()) {
			if (k.isEnabled()) {
				TextAreaLine l = new TextAreaLine(keyList) {
					@Override
					public void onDoubleClick() {
						EnhancedMC.displayWindow(new HotKeyCreatorWindow((HotKey) getStoredObj()), guiInstance);
					}
					@Override
					public void keyPressed(char typedChar, int keyCode) {
						super.keyPressed(typedChar, keyCode);
						if (keyCode == 28) { //enter
							EnhancedMC.displayWindow(new HotKeyCreatorWindow((HotKey) getStoredObj()), guiInstance);
						}
					}
				};
				l.setText("   " + EnumChatFormatting.GREEN + k.getKeyName());
				l.setLineNumberColor(0xb2b2b2);
				l.setStoredObj(k);
				keyList.addTextLine(l);
			}
		}
		if (anyDisabled) { keyList.addTextLine("Disabled Hotkeys:").setLineNumberColor(0xb2b2b2).setTextColor(0x00ffdc); }
		for (HotKey k : mod.getRegisteredHotKeys()) {
			if (!k.isEnabled()) {
				TextAreaLine l = new TextAreaLine(keyList) {
					@Override
					public void onDoubleClick() {
						EnhancedMC.displayWindow(new HotKeyCreatorWindow((HotKey) getStoredObj()));
					}
					@Override
					public void keyPressed(char typedChar, int keyCode) {
						super.keyPressed(typedChar, keyCode);
						if (keyCode == 28) { //enter
							EnhancedMC.displayWindow(new HotKeyCreatorWindow((HotKey) getStoredObj()));
						}
					}
				};
				l.setText("   " + EnumChatFormatting.RED + k.getKeyName());
				l.setLineNumberColor(0xb2b2b2);
				l.setStoredObj(k);
				keyList.addTextLine(l);
			}
		}
	}
	
	private void sortListByApp() {
		
	}
	
	private void sortListByCategory() {
		
	}
	
	private void sortListByNameUp() {
		
	}
	
	private void sortListByNameDown() {
		
	}
	
}
