package it.albertus.cyclesmod.engine;

import java.beans.Introspector;

import it.albertus.cyclesmod.model.Bike;
import it.albertus.cyclesmod.model.BikesInf;
import it.albertus.cyclesmod.model.Gearbox;
import it.albertus.cyclesmod.model.Setting;
import it.albertus.cyclesmod.model.Settings;
import it.albertus.cyclesmod.model.Torque;
import it.albertus.cyclesmod.resources.Messages;
import it.albertus.util.StringUtils;

public abstract class CyclesModEngine implements NumeralSystemProvider {

	private static final String MSG_KEY_ERR_UNSUPPORTED_PROPERTY = "err.unsupported.property";

	private NumeralSystem numeralSystem = NumeralSystem.DEFAULT;
	private BikesInf bikesInf;

	@Override
	public NumeralSystem getNumeralSystem() {
		return numeralSystem;
	}

	public void setNumeralSystem(final NumeralSystem numeralSystem) {
		this.numeralSystem = numeralSystem;
	}

	public BikesInf getBikesInf() {
		return bikesInf;
	}

	public void setBikesInf(final BikesInf bikesInf) {
		this.bikesInf = bikesInf;
	}

	public static boolean isNumeric(final String value, final int radix) {
		try {
			Long.parseLong(value, radix);
		}
		catch (final NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public boolean isNumeric(final String value) {
		return isNumeric(value, numeralSystem.getRadix());
	}

	protected boolean applyProperty(final String key, final String value, final boolean lenient) {
		boolean applied = false;
		try {
			if (value == null || value.trim().length() == 0 || !isNumeric(value.trim())) {
				throw new InvalidPropertyException(Messages.get(MSG_KEY_ERR_UNSUPPORTED_PROPERTY, key, value));
			}

			// Settings
			if (isSettingsProperty(key)) {
				applied = applySettingProperty(key, value);
			}

			// Gearbox
			else if (isGearboxProperty(key)) {
				applied = applyGearboxProperty(key, value);
			}

			// Torque
			else if (isTorqueProperty(key)) {
				applied = applyTorqueProperty(key, value);
			}

			else {
				throw new InvalidPropertyException(Messages.get(MSG_KEY_ERR_UNSUPPORTED_PROPERTY, key, value));
			}
		}
		catch (InvalidPropertyException ipe) {
			if (!lenient) {
				throw ipe;
			}
		}
		return applied;
	}

	public boolean isTorqueProperty(final String key) {
		return StringUtils.substringAfter(key, ".").startsWith(Introspector.decapitalize(Torque.class.getSimpleName()));
	}

	public boolean isGearboxProperty(final String key) {
		return StringUtils.substringAfter(key, ".").startsWith(Introspector.decapitalize(Gearbox.class.getSimpleName()));
	}

	public boolean isSettingsProperty(final String key) {
		return StringUtils.substringAfter(key, ".").startsWith(Introspector.decapitalize(Settings.class.getSimpleName()));
	}

	private boolean applyTorqueProperty(final String key, final String value) {
		boolean applied = false;
		final short newValue = Torque.parse(key, value, numeralSystem.getRadix());

		final Bike bike = getBike(key, value);
		final String suffix = StringUtils.substringAfter(key, Introspector.decapitalize(Torque.class.getSimpleName()) + '.');
		if (StringUtils.isNotEmpty(suffix) && StringUtils.isNumeric(suffix) && Integer.parseInt(suffix) < bike.getTorque().getCurve().length) {
			final int index = Integer.parseInt(suffix);
			final short defaultValue = bike.getTorque().getCurve()[index];
			if (defaultValue != newValue) {
				bike.getTorque().getCurve()[index] = newValue;
				applied = true;
				logChange(key, defaultValue, newValue);
			}
		}
		else {
			throw new InvalidPropertyException(Messages.get(MSG_KEY_ERR_UNSUPPORTED_PROPERTY, key, value));
		}
		return applied;
	}

	private boolean applyGearboxProperty(final String key, final String value) {
		boolean applied = false;
		final int newValue = Gearbox.parse(key, value, numeralSystem.getRadix());

		final Bike bike = getBike(key, value);
		final String suffix = StringUtils.substringAfter(key, Introspector.decapitalize(Gearbox.class.getSimpleName()) + '.');
		if (StringUtils.isNotEmpty(suffix) && StringUtils.isNumeric(suffix) && Integer.parseInt(suffix) < bike.getGearbox().getRatios().length) {
			final int index = Integer.parseInt(suffix);
			final int defaultValue = bike.getGearbox().getRatios()[index];
			if (defaultValue != newValue) {
				bike.getGearbox().getRatios()[index] = newValue;
				applied = true;
				logChange(key, defaultValue, newValue);
			}
		}
		else {
			throw new InvalidPropertyException(Messages.get(MSG_KEY_ERR_UNSUPPORTED_PROPERTY, key, value));
		}
		return applied;
	}

	private boolean applySettingProperty(final String key, final String value) {
		boolean applied = false;
		final int newValue = Settings.parse(key, value, numeralSystem.getRadix());

		final Bike bike = getBike(key, value);
		final String suffix = StringUtils.substringAfter(key, Introspector.decapitalize(Settings.class.getSimpleName()) + '.');
		final Setting setting = Setting.getSetting(suffix);
		if (setting != null) {
			final int defaultValue = bike.getSettings().getValues().get(setting);
			if (newValue != defaultValue) {
				bike.getSettings().getValues().put(setting, newValue);
				applied = true;
				logChange(key, defaultValue, newValue);
			}
		}
		else {
			throw new InvalidPropertyException(Messages.get(MSG_KEY_ERR_UNSUPPORTED_PROPERTY, key, value));
		}
		return applied;
	}

	private void logChange(final String key, final int defaultValue, final int newValue) {
		System.out.println(Messages.get("msg.custom.value.detected", key, newValue, String.format("%X", newValue), defaultValue, String.format("%X", defaultValue)));
	}

	private Bike getBike(final String key, final String value) {
		final Bike bike = bikesInf.getBike(Integer.parseInt(StringUtils.substringBefore(key, ".")));
		if (bike == null) {
			throw new InvalidPropertyException(Messages.get(MSG_KEY_ERR_UNSUPPORTED_PROPERTY, key, value));
		}
		return bike;
	}

}