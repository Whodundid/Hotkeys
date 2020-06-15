package com.Whodundid.hotkeys.window;

import com.Whodundid.core.EnhancedMC;
import com.Whodundid.core.app.AppType;
import com.Whodundid.core.app.RegisteredApps;
import com.Whodundid.core.util.renderUtil.CenterType;
import com.Whodundid.core.util.renderUtil.EColors;
import com.Whodundid.core.util.storageUtil.EDimension;
import com.Whodundid.core.windowLibrary.windowObjects.actionObjects.WindowButton;
import com.Whodundid.core.windowLibrary.windowObjects.basicObjects.WindowContainer;
import com.Whodundid.core.windowLibrary.windowObjects.basicObjects.WindowLabel;
import com.Whodundid.core.windowLibrary.windowObjects.windows.WindowDialogueBox;
import com.Whodundid.core.windowLibrary.windowObjects.windows.WindowDialogueBox.DialogueBoxTypes;
import com.Whodundid.core.windowLibrary.windowTypes.WindowParent;
import com.Whodundid.core.windowLibrary.windowTypes.interfaces.IActionObject;
import com.Whodundid.hotkeys.HotKeyApp;
import com.Whodundid.hotkeys.util.HKResources;

public class HotKeySettingsWindow extends WindowParent {
	
	HotKeyApp mod = (HotKeyApp) RegisteredApps.getApp(AppType.HOTKEYS);
	WindowContainer settings, keyList;
	WindowButton stopMovement, createExample;
	WindowButton loadKeyList, saveKeyList, resetKeyList;
	WindowLabel stopMovementLabel, runKeyBuilderLabel;
	WindowDialogueBox msgBox;
	
	public HotKeySettingsWindow() {
		super();
		aliases.add("hotkeysettings", "keysettings", "hksettings");
		windowIcon = HKResources.icon;
	}
	
	@Override
	public void initWindow() {
		setObjectName("Hotkey Settings");
		defaultDims();
	}
	
	@Override
	public void initObjects() {
		defaultHeader(this);
		
		settings = new WindowContainer(this, startX + 2, startY + 2, width - 4, 140);
		settings.setTitle("General");
		settings.setTitleColor(EColors.orange.intVal);
		settings.setTitleBackgroundColor(0xff191919);
		settings.setTitleCentered(true);
		settings.setBackgroundColor(EColors.pdgray.intVal);
		EDimension sDim = settings.getDimensions();
		
		stopMovement = new WindowButton(settings, sDim.startX + 10, sDim.startY + 26, 55, 20).setTrueFalseButton(true).updateTrueFalseDisplay(mod.doesCtrlKeyStopMovement());
		stopMovementLabel = new WindowLabel(settings, stopMovement.endX + (endX - stopMovement.endX) / 2, stopMovement.startY + 4, "Stop movement when Ctrl is pressed", 0xb2b2b2);
		
		createExample = new WindowButton(settings, sDim.startX + 10, stopMovement.endY + 6, 55, 20).setTrueFalseButton(true).updateTrueFalseDisplay(mod.runTutorial());
		runKeyBuilderLabel = new WindowLabel(settings, createExample.endX + 15, createExample.startY + 6, "Enable creation tutorial", 0xb2b2b2);
		
		stopMovementLabel.enableShadow(true).enableWordWrap(true, 125).setDrawCentered(true);
		runKeyBuilderLabel.enableShadow(true);
		
		IActionObject.setActionReceiver(this, stopMovement, createExample);
		
		settings.addObject(null, stopMovement, stopMovementLabel, createExample, runKeyBuilderLabel);
		
		keyList = new WindowContainer(this, startX + 2, settings.getDimensions().endY + 1, width - 4, (guiInstance.endY - 2) - (settings.getDimensions().endY + 1));
		keyList.setTitle("Hotkey Lists");
		keyList.setTitleColor(EColors.orange.intVal);
		keyList.setTitleBackgroundColor(0xff191919);
		keyList.setTitleCentered(true);
		keyList.setBackgroundColor(0xff252525);
		EDimension kDim = keyList.getDimensions();
		
		loadKeyList = new WindowButton(keyList, kDim.midX - (110 / 2), kDim.startY + 29, 110, 20, "Reload Hotkeys");
		saveKeyList = new WindowButton(keyList, kDim.midX - (110 / 2), loadKeyList.endY + 4, 110, 20, "Save Hotkeys");
		resetKeyList = new WindowButton(keyList, kDim.midX - (110 / 2), saveKeyList.endY + 4, 110, 20, "Reset Hotkeys");
		
		loadKeyList.setStringColor(EColors.yellow);
		saveKeyList.setStringColor(EColors.yellow);
		resetKeyList.setStringColor(EColors.lred);
		
		IActionObject.setActionReceiver(this, loadKeyList, saveKeyList, resetKeyList);
		
		keyList.addObject(null, loadKeyList, saveKeyList, resetKeyList);
		
		addObject(null, settings, keyList);
	}
	
	@Override
	public void drawObject(int mXIn, int mYIn) {
		drawDefaultBackground();
		super.drawObject(mXIn, mYIn);
	}
	
	@Override
	public void actionPerformed(IActionObject object, Object... args) {
		if (object == stopMovement) {
			mod.setStopMovementOnPress(!mod.doesCtrlKeyStopMovement());
			stopMovement.updateTrueFalseDisplay(mod.doesCtrlKeyStopMovement());
			mod.getConfig().saveMainConfig();
		}
		if (object == createExample) {
			mod.setRunTutorial(!mod.runTutorial());
			createExample.updateTrueFalseDisplay(mod.runTutorial());
			mod.getConfig().saveMainConfig();
		}
		if (object == loadKeyList) { reloadKeys(); }
		if (object == saveKeyList) { saveKeys(); }
		if (object == resetKeyList) { resetKeys(); }
	}
	
	public void reloadKeys() {
		mod.loadHotKeys();
		msgBox = new WindowDialogueBox(DialogueBoxTypes.ok);
		msgBox.setTitle("Reload Hotkeys");
		msgBox.setTitleColor(EColors.lgray.intVal);
		msgBox.setMessage("Success! Hotkeys reloaded.").setMessageColor(0x55ff55);
		EnhancedMC.displayWindow(msgBox, CenterType.screen);
	}
	
	public void saveKeys() {
		mod.saveHotKeys();
		msgBox = new WindowDialogueBox(DialogueBoxTypes.ok);
		msgBox.setTitle("Save Hotkeys");
		msgBox.setTitleColor(EColors.lgray.intVal);
		msgBox.setMessage("Success! Hotkeys saved.").setMessageColor(0x55ff55);
		EnhancedMC.displayWindow(msgBox, CenterType.screen);
	}
	
	public void resetKeys() {
		msgBox = new WindowDialogueBox(DialogueBoxTypes.yesNo) {
			@Override
			public void actionPerformed(IActionObject object, Object... args) {
				if (object == yes) {
					this.close();
					mod.reset();
					msgBox = new WindowDialogueBox(DialogueBoxTypes.ok);
					msgBox.setTitle("Reset Hotkeys");
					msgBox.setTitleColor(EColors.lgray.intVal);
					msgBox.setMessage("Success! All user hotkeys have been deleted.").setMessageColor(0x55ff55);
					EnhancedMC.displayWindow(msgBox, CenterType.screen);
				}
				if (object == no) { this.close(); }
			}
		};
		msgBox.setTitle("HotKey Reset");
		msgBox.setTitleColor(EColors.lgray.intVal);
		msgBox.setMessage("Are you sure you want to reset hotkeys? This will delete all user made hotkeys and clear the user key file! CANNOT BE UNDONE!").setMessageColor(0xff5555);
		guiInstance.addObject(msgBox);
	}
	
}
