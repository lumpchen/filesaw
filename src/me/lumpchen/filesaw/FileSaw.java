package me.lumpchen.filesaw;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileSaw {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args == null || args.length != 3) {
			showHelp();
			return;
		}

		String path = args[0];
		int size = Integer.parseInt(args[1]) * 1024 * 1024;

		if (args[2].equalsIgnoreCase("-asbyte")) {
			readAsBinary(path, size);
		}

		if (args[2].equalsIgnoreCase("-asstring")) {
			readAsString(path, size);
		}
	}

	static void readAsString(String path, int size) {
		File f = new File(path);
		if (!f.exists()) {
			System.err.println("File not found " + path);
		}

		FileReader fr = null;
		try {
			fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			String name = f.getName();
			int segNum = 1;
			File seg = new File(f.getParentFile().getAbsolutePath() + "/"
					+ name + "." + segNum);
			if (!seg.createNewFile()) {
				System.err.println("Cannot create file: "
						+ seg.getAbsolutePath());
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(seg));

			StringBuffer sb = new StringBuffer();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");

				if (sb.length() > size) {
					out.write(sb.toString());
					sb = new StringBuffer();

					out.flush();
					out.close();

					System.out.println(seg.getAbsolutePath());
					segNum++;
					seg = new File(f.getParentFile().getAbsolutePath() + "/"
							+ name + "." + segNum);
					if (!seg.createNewFile()) {
						System.err.println("Cannot create file: "
								+ seg.getAbsolutePath());
					}
					out = new BufferedWriter(new FileWriter(seg));
				}

				line = br.readLine();
			}
			
			if (sb.length() > 0) {
				out.write(sb.toString());
				out.flush();
				out.close();
				
				System.out.println(seg.getAbsolutePath());
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	static void readAsBinary(String path, int size) {
		File f = new File(path);
		if (!f.exists()) {
			System.err.println("File not found " + path);
		}

		BufferedInputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(f));

			String name = f.getName();
			int segNum = 1;
			File seg = new File(f.getParentFile().getAbsolutePath() + "/"
					+ name + "." + segNum);
			if (!seg.createNewFile()) {
				System.err.println("Cannot create file: "
						+ seg.getAbsolutePath());
			}
			BufferedOutputStream out = new BufferedOutputStream(
					new FileOutputStream(seg));

			int read = 0;
			int count = 0;
			byte[] buf = new byte[4096];
			while ((read = is.read(buf)) != -1) {
				count += read;

				out.write(buf);
				if (count > size) {
					out.flush();
					out.close();

					System.out.println(seg.getAbsolutePath());
					segNum++;
					seg = new File(f.getParentFile().getAbsolutePath() + "/"
							+ name + "." + segNum);
					if (!seg.createNewFile()) {
						System.err.println("Cannot create file: "
								+ seg.getAbsolutePath());
					}
					out = new BufferedOutputStream(new FileOutputStream(seg));
					count = 0;
				}
			}

			if (count > 0) {
				out.flush();
				out.close();
				System.out.println(seg.getAbsolutePath());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	static void showHelp() {
		System.out.println("usage:");
		System.out.println("FileSaw \"file name\" 100 -asstring");
		System.out.println("or");
		System.out.println("FileSaw \"file name\" 100 -asbyte");
	}
}
