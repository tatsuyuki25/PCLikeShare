package com.likeshare.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import com.likeshare.LoginUI;
import com.likeshare.tree.DevicesManager;

public class nioClientReadThread implements Runnable {
	private Selector selector;
	private DevicesManager dm;
	private LikeShareService lss;
	public nioClientReadThread(Selector selector,DevicesManager dm,LikeShareService lss) {
		this.selector = selector;
		this.dm = dm;
		this.lss = lss;
		new Thread(this).start();
	}

	public void run() {
		try {
			while (selector.select() > 0) {
				// 遍歷每個有可用IO操作Channel對應的SelectionKey
				for (SelectionKey sk : selector.selectedKeys()) {

					// 如果該SelectionKey對應的Channel中有可讀的數據
					if (sk.isReadable()) {
						// 使用NIO讀取Channel中的數據
						SocketChannel sc = (SocketChannel) sk.channel();
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						sc.read(buffer);
						buffer.flip();
						// 將字節轉化為為UTF-16的字符串
						String receivedString = Charset.forName("UTF-16")
								.newDecoder().decode(buffer).toString();
						// 控制台打印出來
						System.out.println("接收到來自服務器"
								+ sc.socket().getRemoteSocketAddress() + "的信息:"
								+ receivedString);

						String tmp[] = receivedString.split(",");
						switch (Integer.parseInt(tmp[0])) {
						case 0: // login
							LoginUI.login(Boolean.parseBoolean(tmp[1]));
							break;
						case 1: // 收到好友上線通知 tmp[1] = MAC
							dm.deviceLogin(tmp[1]);
							break;
						case 2: // 請求我的所有設備
							dm.createMyDevices(receivedString);
							break; // 收到 我的所有設備名單
						case 3: // 註冊成功失敗通道

							break;
						case 4: // 收到好友清單
							dm.createMyFriends(receivedString);
							break;
						case 5: // 收到好友擁有的設備
							dm.setFriendUp(receivedString);
							break;
						case 6: // 連接伺服器指定的port
							lss.startBioFile(receivedString);
							break;
						case 7: // 取消下載
							lss.stopBioFileService();
							break;
						case 9:
							lss.bioVideoService(receivedString);
							break;
						}

						// 為下一次讀取作準備
						sk.interestOps(SelectionKey.OP_READ);
					}
					// 刪除正在處理的SelectionKey
					selector.selectedKeys().remove(sk);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}