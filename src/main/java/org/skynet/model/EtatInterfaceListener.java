package org.skynet.model;

import org.skynet.model.ControlleurEtatInterface.State;

public interface EtatInterfaceListener {
	public void onStateChanged(org.skynet.model.ControlleurEtatInterface etatInterface,State state);
	public void onReset(org.skynet.model.ControlleurEtatInterface etatInterface);
}
