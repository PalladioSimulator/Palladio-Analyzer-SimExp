/**
 */
package org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams;

import org.eclipse.emf.ecore.EObject;

import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Distribution Factor Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue#getValue <em>Value</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue#getAppliedBranch <em>Applied Branch</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage#getDistributionFactorValue()
 * @model
 * @generated
 */
public interface DistributionFactorValue extends EObject {
	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(double)
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage#getDistributionFactorValue_Value()
	 * @model required="true"
	 * @generated
	 */
	double getValue();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(double value);

	/**
	 * Returns the value of the '<em><b>Applied Branch</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Applied Branch</em>' reference.
	 * @see #setAppliedBranch(ProbabilisticBranchTransition)
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage#getDistributionFactorValue_AppliedBranch()
	 * @model required="true"
	 * @generated
	 */
	ProbabilisticBranchTransition getAppliedBranch();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue#getAppliedBranch <em>Applied Branch</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Applied Branch</em>' reference.
	 * @see #getAppliedBranch()
	 * @generated
	 */
	void setAppliedBranch(ProbabilisticBranchTransition value);

} // DistributionFactorValue
