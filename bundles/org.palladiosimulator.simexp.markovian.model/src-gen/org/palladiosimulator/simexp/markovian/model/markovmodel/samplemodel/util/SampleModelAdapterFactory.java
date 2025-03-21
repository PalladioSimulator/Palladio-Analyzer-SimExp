/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModelPackage
 * @generated
 */
public class SampleModelAdapterFactory extends AdapterFactoryImpl {
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static SampleModelPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SampleModelAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = SampleModelPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject) object).eClass()
                .getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SampleModelSwitch<Adapter> modelSwitch = new SampleModelSwitch<Adapter>() {
        @Override
        public <A, R> Adapter caseTrajectory(Trajectory<A, R> object) {
            return createTrajectoryAdapter();
        }

        @Override
        public <A, R> Adapter caseSample(Sample<A, R> object) {
            return createSampleAdapter();
        }

        @Override
        public <A, R> Adapter caseSampleModel(SampleModel<A, R> object) {
            return createSampleModelAdapter();
        }

        @Override
        public Adapter defaultCase(EObject object) {
            return createEObjectAdapter();
        }
    };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        return modelSwitch.doSwitch((EObject) target);
    }

    /**
     * Creates a new adapter for an object of class '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory <em>Trajectory</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory
     * @generated
     */
    public Adapter createTrajectoryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample <em>Sample</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample
     * @generated
     */
    public Adapter createSampleAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel <em>Sample Model</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel
     * @generated
     */
    public Adapter createSampleModelAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }

} //SampleModelAdapterFactory
