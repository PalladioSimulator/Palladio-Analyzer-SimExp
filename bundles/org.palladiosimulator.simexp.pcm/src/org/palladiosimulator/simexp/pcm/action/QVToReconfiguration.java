package org.palladiosimulator.simexp.pcm.action;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;

public class QVToReconfiguration extends Reconfiguration<QVTOReconfigurator> {

	private final static String EMPTY_RECONFIGURATION_NAME = "EmptyReconf";

	private final QvtoModelTransformation transformation;

	protected QVToReconfiguration(QvtoModelTransformation transformation) {
		this.transformation = transformation;
	}

	public static QVToReconfiguration of(QvtoModelTransformation transformation) {
		return new QVToReconfiguration(transformation);
	}

	public static QVToReconfiguration empty() {
		return new QVToReconfiguration(null);
	}

	public QvtoModelTransformation getTransformation() {
		return transformation;
	}

	public boolean isEmptyReconfiguration() {
		return transformation == null;
	}

	@Override
	public String getStringRepresentation() {
		if (isEmptyReconfiguration()) {
			return EMPTY_RECONFIGURATION_NAME;
		}
		return transformation.getTransformationName();
	}

}
