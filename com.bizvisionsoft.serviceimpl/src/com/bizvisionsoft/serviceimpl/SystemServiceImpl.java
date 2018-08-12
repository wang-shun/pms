package com.bizvisionsoft.serviceimpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bizvisionsoft.service.SystemService;
import com.bizvisionsoft.service.model.Backup;
import com.bizvisionsoft.service.model.ServerInfo;
import com.bizvisionsoft.service.tools.Util;
import com.bizvisionsoft.serviceimpl.exception.ServiceException;
import com.bizvisionsoft.serviceimpl.mongotools.MongoDBBackup;
import com.mongodb.MongoClient;

public class SystemServiceImpl extends BasicServiceImpl implements SystemService {

	@Override
	public ServerInfo getServerInfo(String req) {
		return new ServerInfo(req).setHostMessage("Hello " + req);

	}

	@Override
	public String mongodbDump(String note) {
		final MongoClient client = Service.getMongo();
		String host = client.getAddress().getHost();
		int port = client.getAddress().getPort();
		String dbName = Service.db().getName();
		String dumpPath = Service.dumpFolder.getPath();
		String path = Service.mongoDbBinPath;
		String result = new MongoDBBackup.Builder().runtime(Runtime.getRuntime()).path(path).host(host).port(port)
				.dbName(dbName).archive(dumpPath + "\\").build().dump();
		try {
			Util.writeFile(note, result + "/notes.txt", "utf-8");
		} catch (IOException e) {
			throw new ServiceException(e.getMessage());
		}

		return result;
	}

	@Override
	public List<Backup> getBackups() {
		List<Backup> result = new ArrayList<>();
		File folder = Service.dumpFolder;
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				try {
					String name = files[i].getName();
					Backup backup = new Backup().setDate(new Date(Long.parseLong(name)));
					backup.setId(name);
					File[] note = files[i].listFiles(f -> f.getName().equals("notes.txt"));
					if (note != null && note.length > 0) {
						String text = Util.readFile(note[0].getPath(), "utf-8");
						backup.setNotes(text);
					}
					result.add(backup);
				} catch (Exception e) {
				}
			}
		}
		return result;
	}

	@Override
	public void updateBackupNote(String id, String text) {
		File[] files = Service.dumpFolder.listFiles(f -> f.isDirectory() && id.equals(f.getName()));
		if (files != null && files.length > 0) {
			try {
				Util.writeFile(text, files[0].getPath() + "/notes.txt", "utf-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean deleteBackup(String id) {
		File[] files = Service.dumpFolder.listFiles(f -> f.isDirectory() && id.equals(f.getName()));
		if (files != null && files.length > 0) {
			return Util.deleteFolder(files[0].getPath());
		}
		return false;
	}

	@Override
	public boolean restoreFromBackup(String id) {
		final MongoClient client = Service.getMongo();
		String host = client.getAddress().getHost();
		int port = client.getAddress().getPort();
		String dbName = Service.db().getName();
		String path = Service.mongoDbBinPath;
		String archive;
		File[] files = Service.dumpFolder.listFiles(f -> f.isDirectory() && id.equals(f.getName()));
		if (files != null && files.length > 0) {
			archive = files[0].getPath()+"\\"+dbName;
		} else {
			return false;
		}

		new MongoDBBackup.Builder().runtime(Runtime.getRuntime()).path(path).host(host).port(port).dbName(dbName)
				.archive(archive).build().restore();
		return true;
	}

}