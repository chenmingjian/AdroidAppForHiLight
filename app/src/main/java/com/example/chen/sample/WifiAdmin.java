package com.example.chen.sample;

import java.net.Inet4Address;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

public class WifiAdmin {

	public  WifiManager wifiManager;
	private WifiInfo wifiInfo;

	private List<ScanResult> scanResultList;

	private List<WifiConfiguration> wifiConfigList;

	private WifiLock wifiLock;

	public enum WifiCipherType {
		WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
	}


	public boolean Connect(String SSID, String Password, WifiCipherType Type) {
		if (!this.OpenWifi()) {
			return false;
		}

		while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
			try {
				Thread.currentThread();
				Thread.sleep(100);
			} catch (InterruptedException ie) {
			}
		}

		WifiConfiguration wifiConfig = this.CreateWifiInfo(SSID, Password, Type);
		//
		if (wifiConfig == null) {
			return false;
		}
		WifiConfiguration tempConfig = this.IsExsits(SSID);
		if (tempConfig != null) {
			wifiManager.removeNetwork(tempConfig.networkId);
		}
		int netID = wifiManager.addNetwork(wifiConfig);
		System.out.println(netID);
		wifiManager.startScan();

		for (WifiConfiguration c0 : wifiManager.getConfiguredNetworks()) {
			if (c0.networkId == netID) {
				boolean bRet = wifiManager.enableNetwork(c0.networkId, true);		//why TWICE?
			} else {
				wifiManager.enableNetwork(c0.networkId, false);
			}
		}
		boolean bRet = wifiManager.enableNetwork(netID, true);		
		wifiManager.saveConfiguration();
		return bRet;
	}

	public boolean Connect(WifiConfiguration wf) {
		if (!this.OpenWifi()) {
			return false;
		}
		while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
			try {
				Thread.currentThread();
				Thread.sleep(100);
			} catch (InterruptedException ie) {
			}
		}

		WifiConfiguration wifiConfig = wf;
		//
		if (wifiConfig == null) {
			return false;
		}
		WifiConfiguration tempConfig = this.IsExsits(wifiConfig.SSID);
		if (tempConfig != null) {
			wifiManager.removeNetwork(tempConfig.networkId);
		}
		int netID = wifiManager.addNetwork(wifiConfig);
		System.out.println(netID);
		wifiManager.startScan();

		for (WifiConfiguration c0 : wifiManager.getConfiguredNetworks()) {
			if (c0.networkId == netID) {
				boolean bRet = wifiManager.enableNetwork(c0.networkId, true);
			} else {
				wifiManager.enableNetwork(c0.networkId, false);
			}
		}
		boolean bRet = wifiManager.enableNetwork(netID, true);
		wifiManager.saveConfiguration();
		return bRet;
	}

	public boolean OpenWifi() {
		boolean bRet = true;
		if (!wifiManager.isWifiEnabled()) {
			bRet = wifiManager.setWifiEnabled(true);
		}
		return bRet;
	}


	public WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	private WifiConfiguration CreateWifiInfo(String SSID, String Password, WifiCipherType Type) {
		WifiConfiguration wc = new WifiConfiguration();
		wc.allowedAuthAlgorithms.clear();
		wc.allowedGroupCiphers.clear();
		wc.allowedKeyManagement.clear();
		wc.allowedPairwiseCiphers.clear();
		wc.allowedProtocols.clear();
		wc.SSID = "\"" + SSID + "\"";
		if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
			wc.wepKeys[0] = "";
			wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			wc.wepTxKeyIndex = 0;
		} else if (Type == WifiCipherType.WIFICIPHER_WEP) {
			wc.wepKeys[0] = "\"" + Password + "\"";
			wc.hiddenSSID = true;
			System.out.println("111111111111111111111111");
			wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			wc.wepTxKeyIndex = 0;
			System.out.println(wc);
		} else if (Type == WifiCipherType.WIFICIPHER_WPA) {
			wc.preSharedKey = "\"" + Password + "\"";
			wc.hiddenSSID = true;
			wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA); // for WPA
			wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN); // for WPA2
		} else {
			return null;
		}
		return wc;
	}

	public WifiAdmin(Context context) {
		this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		this.wifiInfo = wifiManager.getConnectionInfo();
	}


	public boolean getWifiStatus() {
		return wifiManager.isWifiEnabled();
	}

	public int getWifiState() {
		return wifiManager.getWifiState();
	}

	public boolean closeWifi() {
		if (!wifiManager.isWifiEnabled()) {
			return true;
		} else {
			return wifiManager.setWifiEnabled(false);
		}
	}


	public void lockWifi() {
		wifiLock.acquire();
	}

	public void unLockWifi() {
		if (!wifiLock.isHeld()) {
			wifiLock.release();

		}
	}


	public void createWifiLock() {
		wifiLock = wifiManager.createWifiLock("flyfly");

	}


	public void startScan() {
		wifiManager.startScan();
		scanResultList = wifiManager.getScanResults();
		wifiConfigList = wifiManager.getConfiguredNetworks();

	}


	public List<ScanResult> getWifiList() {
		return scanResultList;
	}


	public List<WifiConfiguration> getWifiConfigList() {
		return wifiConfigList;
	}


	public StringBuilder lookUpscan() {
		StringBuilder scanBuilder = new StringBuilder();
		for (int i = 0; i < scanResultList.size(); i++) {
			scanBuilder.append("��ţ�" + (i + 1));
			scanBuilder.append(scanResultList.get(i).toString());
			scanBuilder.append("\n");
		}
		return scanBuilder;
	}

	public int getLevel(int NetId) {
		return scanResultList.get(NetId).level;
	}

	public String getMac() {
		return (wifiInfo == null) ? "" : wifiInfo.getMacAddress();
	}

	public String getBSSID() {
		return (wifiInfo == null) ? null : wifiInfo.getBSSID();
	}


	public String getSSID() {
		return (wifiInfo == null) ? null : wifiInfo.getSSID();
	}

	public int getCurrentNetId() {
		return (wifiInfo == null) ? null : wifiInfo.getNetworkId();
	}

	public String getwifiInfo() {
		return (wifiInfo == null) ? null : wifiInfo.toString();
	}


	public int getIP() {
		return (wifiInfo == null) ? null : wifiInfo.getIpAddress();
	}


	public boolean addNetWordLink(WifiConfiguration config) {
		int NetId = wifiManager.addNetwork(config);
		return wifiManager.enableNetwork(NetId, true);
	}


	public boolean disableNetWordLick(int NetId) {
		wifiManager.disableNetwork(NetId);
		return wifiManager.disconnect();
	}

	public boolean removeNetworkLink(int NetId) {
		return wifiManager.removeNetwork(NetId);
	}


	public void hiddenSSID(int NetId) {
		wifiConfigList.get(NetId).hiddenSSID = true;
	}


	public void displaySSID(int NetId) {
		wifiConfigList.get(NetId).hiddenSSID = false;
	}


	public String ipIntToString(int ip) {
		try {
			byte[] bytes = new byte[4];
			bytes[0] = (byte) (0xff & ip);
			bytes[1] = (byte) ((0xff00 & ip) >> 8);
			bytes[2] = (byte) ((0xff0000 & ip) >> 16);
			bytes[3] = (byte) ((0xff000000 & ip) >> 24);
			return Inet4Address.getByAddress(bytes).getHostAddress();
		} catch (Exception e) {
			return "";
		}
	}
}
