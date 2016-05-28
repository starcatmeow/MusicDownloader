package top.starcatmeow.music.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.json.simple.JSONValue;

import net.sf.json.*;

@SuppressWarnings("serial")
public class download extends JFrame implements ActionListener {
	int a = 0;
	Object[] name = { "歌名", "歌手", "下载地址" };
	private JLabel lb;
	private JTextField tf;
	private JButton bt;
	private JTable jt;
	private JPanel tpe;
	private JPanel pe;
	private JPopupMenu m_popupmenu;
	String temps;
	Object temp, temp2;
	JSONObject tempjo, tempjo2;

	Object[][] tdata = new Object[5000][3];

	public download() {
		lb = new JLabel("请输入歌曲名称：");
		tf = new JTextField(55);
		bt = new JButton("搜索");
		jt = new JTable(tdata, name);
		jt.setPreferredScrollableViewportSize(new Dimension(800, 375));
		jt.setEnabled(false);
		pe = new JPanel();
		tpe = new JPanel();
		tpe.add(jt);
		tpe.add(new JScrollPane(jt), BorderLayout.CENTER);
		bt.addActionListener(this);
		pe.add(lb);
		pe.add(tf);
		pe.add(bt);
		pe.add(tpe);
		createPopupMenu();
		this.getContentPane().add(pe);
		this.setTitle("付费音乐免费下载器（基于网易云音乐）");
		jt.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				jtMouseClicked(evt);
			}
		});
	}

	public void jtMouseClicked(java.awt.event.MouseEvent evt) {

		mouseRightButtonClick(evt);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		download download = new download();
		download.setSize(854, 480);
		download.setResizable(false);
		download.setVisible(true);
	}

	private void mouseRightButtonClick(java.awt.event.MouseEvent evt) {
		if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
			int focusedRowIndex = jt.rowAtPoint(evt.getPoint());
			if (focusedRowIndex == -1) {
				return;
			}
			jt.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
			m_popupmenu.show(jt, evt.getX(), evt.getY());
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object obj = e.getSource();
		if (obj instanceof JButton) {
			if (obj == bt) {
				String url = "http://s.music.163.com/search/get/?type=1&s=" + tf.getText() + "&limit=500&offest=0";
				String json = loadJSON(url);
				int numa = 0, numb = 0;
				for (numb = 0; numa < 3; numb++) {
					if (json.charAt(numb) == ':') {
						numa++;
					}
				}
				json = json.substring(numb, json.length() - 13);
				Object obj2 = JSONValue.parse(json);
				JSONArray jsonarray = JSONArray.fromObject(obj2);
				for (int i = 0; i <= jsonarray.size() - 1; i++) {
					temp = jsonarray.get(i);
					tempjo = JSONObject.fromObject(temp);
					jt.setValueAt((Object) tempjo.getString("name"), i, 0);
					temps = tempjo.getString("artists");
					temps = temps.substring(1, temps.length() - 1);
					temp2 = JSONObject.fromObject(temps);
					tempjo2 = JSONObject.fromObject(temp2);
					jt.setValueAt((Object) tempjo2.getString("name"), i, 1);
					jt.setValueAt((Object) tempjo.getString("audio"), i, 2);
				}
			}
		}
	}

	public static String loadJSON(String url) {
		StringBuilder json = new StringBuilder();
		try {
			URL oracle = new URL(url);
			URLConnection yc = oracle.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				json.append(inputLine);
			}
			in.close();
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
		return json.toString();
	}

	public void createPopupMenu() {
		m_popupmenu = new JPopupMenu();

		JMenuItem delMenItem = new JMenuItem();
		delMenItem.setText("  下载此歌曲  ");
		delMenItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					downLoadFromUrl((String) jt.getValueAt(jt.getSelectedRow(), 2),
							jt.getValueAt(jt.getSelectedRow(), 1) + "-" + jt.getValueAt(jt.getSelectedRow(), 0)
									+ ".mp3",
							"d:/songs");
				} catch (Exception e) {
					System.out.println("网络故障！");
				}
			}
		});
		m_popupmenu.add(delMenItem);
	}

	public static void downLoadFromUrl(String urlStr, String fileName, String savePath) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(3 * 1000);
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		InputStream inputStream = conn.getInputStream();
		byte[] getData = readInputStream(inputStream);
		File saveDir = new File(savePath);
		if (!saveDir.exists()) {
			saveDir.mkdir();
		}
		File file = new File(saveDir + File.separator + fileName);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(getData);
		if (fos != null) {
			fos.close();
		}
		if (inputStream != null) {
			inputStream.close();
		}
		System.out.println(fileName + " 下载成功！");
	}

	public static byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}
}
