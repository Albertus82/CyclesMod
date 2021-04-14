package it.albertus.cyclesmod.common.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import it.albertus.cyclesmod.common.data.DefaultBikes;
import it.albertus.cyclesmod.common.resources.Messages;
import it.albertus.util.ByteUtils;
import it.albertus.util.IOUtils;

public class BikesInf {

	public static final String FILE_NAME = "BIKES.INF";
	public static final short FILE_SIZE = 444;

	private final Bike[] bikes = new Bike[BikeType.values().length];

	public BikesInf(final InputStream bikesInfInputStream) throws IOException {
		read(bikesInfInputStream);
	}

	public BikesInf(final File file) throws IOException {
		try (final InputStream fis = new FileInputStream(file); final InputStream bis = new BufferedInputStream(fis)) {
			read(bis);
		}
	}

	public void reset(final BikeType type) throws IOException {
		try (final InputStream is = new DefaultBikes().getInputStream()) {
			read(is, type);
		}
	}

	private void read(final InputStream inf, BikeType... types) throws IOException {
		final byte[] inf125 = new byte[Bike.LENGTH];
		final byte[] inf250 = new byte[Bike.LENGTH];
		final byte[] inf500 = new byte[Bike.LENGTH];

		final boolean wrongFileSize = inf.read(inf125) != Bike.LENGTH || inf.read(inf250) != Bike.LENGTH || inf.read(inf500) != Bike.LENGTH || inf.read() != -1;
		inf.close();
		if (wrongFileSize) {
			throw new IllegalStateException(Messages.get("err.wrong.file.size"));
		}
		System.out.println(Messages.get("msg.file.read", FILE_NAME));

		if (types == null || types.length == 0) {
			/* Full reading */
			bikes[0] = new Bike(BikeType.CLASS_125, inf125);
			bikes[1] = new Bike(BikeType.CLASS_250, inf250);
			bikes[2] = new Bike(BikeType.CLASS_500, inf500);
		}
		else {
			/* Replace only selected bikes */
			final byte[][] infs = new byte[3][];
			infs[0] = inf125;
			infs[1] = inf250;
			infs[2] = inf500;
			for (final BikeType type : types) {
				bikes[type.ordinal()] = new Bike(type, infs[type.ordinal()]);
			}
		}
		System.out.println(Messages.get("msg.file.parsed", FILE_NAME));
	}

	public void write(final String fileName, final boolean backupExisting) throws IOException {
		final byte[] newBikesInf = this.toByteArray();
		final Checksum crc = new CRC32();
		crc.update(newBikesInf, 0, newBikesInf.length);
		System.out.println(Messages.get("msg.configuration.changed", crc.getValue() == DefaultBikes.CRC ? ' ' + Messages.get("msg.not") + ' ' : ' ', String.format("%08X", crc.getValue())));

		final File file = new File(fileName);
		if (file.exists() && !file.isDirectory()) {
			try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
				try (final InputStream is = new FileInputStream(file)) {
					IOUtils.copy(is, os, FILE_SIZE);
				}
				if (Arrays.equals(os.toByteArray(), newBikesInf)) {
					System.out.println(Messages.get("msg.already.uptodate", FILE_NAME));
				}
				else {
					if (backupExisting) {
						backup(fileName);
					}
					doWrite(fileName, newBikesInf, crc);
				}
			}
		}
		else {
			doWrite(fileName, newBikesInf, crc);
		}
	}

	private void doWrite(final String fileName, final byte[] newBikesInf, final Checksum crc) throws IOException {
		try (final OutputStream fos = new FileOutputStream(fileName); final OutputStream bos = new BufferedOutputStream(fos, FILE_SIZE)) {
			bos.write(newBikesInf);
			System.out.println(Messages.get("msg.new.file.written.into.path", FILE_NAME, "".equals(fileName) ? '.' : fileName, String.format("%08X", crc.getValue())));
		}
	}

	private void backup(final String existingFileName) throws IOException {
		File backupFile;
		int i = 0;
		final File existingFile = new File(existingFileName);
		final String parent = existingFile.getParent();
		final String prefix = parent != null ? parent + File.separator : "";
		do {
			backupFile = new File(prefix + "BIKES" + String.format("%03d", i++) + ".ZIP");
		}
		while (backupFile.exists());

		try (final InputStream fis = new FileInputStream(existingFile); final OutputStream fos = new FileOutputStream(backupFile); final ZipOutputStream zos = new ZipOutputStream(fos)) {
			zos.setLevel(Deflater.BEST_COMPRESSION);
			zos.putNextEntry(new ZipEntry(existingFile.getName()));
			IOUtils.copy(fis, zos, FILE_SIZE);
			zos.closeEntry();
			System.out.println(Messages.get("msg.old.file.backed.up", FILE_NAME, backupFile));
		}
	}

	/**
	 * Ricostruisce il file BIKES.INF a partire dalle 3 configurazioni contenute
	 * nell'oggetto (125, 250, 500).
	 * 
	 * @return L'array di byte corrispondente al file BIKES.INF.
	 */
	private byte[] toByteArray() {
		final List<Byte> byteList = new ArrayList<>(FILE_SIZE);
		for (final Bike bike : bikes) {
			byteList.addAll(bike.toByteList());
		}
		if (byteList.size() != FILE_SIZE) {
			throw new IllegalStateException(Messages.get("err.wrong.file.size.detailed", FILE_NAME, FILE_SIZE, byteList.size()));
		}
		return ByteUtils.toByteArray(byteList);
	}

	public Bike getBike(int displacement) {
		for (final Bike bike : bikes) {
			if (bike.getType().getDisplacement() == displacement) {
				return bike;
			}
		}
		return null;
	}

	public Bike[] getBikes() {
		return bikes;
	}

}