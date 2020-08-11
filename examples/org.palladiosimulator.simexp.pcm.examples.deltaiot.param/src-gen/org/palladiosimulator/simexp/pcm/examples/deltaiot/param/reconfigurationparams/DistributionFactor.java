/**
 */
package org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams;

import org.eclipse.emf.common.util.EList;

import org.palladiosimulator.pcm.repository.RepositoryComponent;

import tools.mdsd.modelingfoundations.identifier.NamedElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Distribution Factor</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor#getFactorValues <em>Factor Values</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor#getAppliedComponent <em>Applied Component</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage#getDistributionFactor()
 * @model
 * @generated
 */
public interface DistributionFactor extends NamedElement {
	/**
	 * Returns the value of the '<em><b>Factor Values</b></em>' containment reference list.
	 * The list contents are of type {@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Factor Values</em>' containment reference list.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage#getDistributionFactor_FactorValues()
	 * @model containment="true" lower="2"
	 * @generated
	 */
	EList<DistributionFactorValue> getFactorValues();

	/**
	 * Returns the value of the '<em><b>Applied Component</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Applied Component</em>' reference.
	 * @see #setAppliedComponent(RepositoryComponent)
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage#getDistributionFactor_AppliedComponent()
	 * @model required="true"
	 * @generated
	 */
	RepositoryComponent getAppliedComponent();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor#getAppliedComponent <em>Applied Component</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Applied Component</em>' reference.
	 * @see #getAppliedComponent()
	 * @generated
	 */
	void setAppliedComponent(RepositoryComponent value);

} // DistributionFactor
