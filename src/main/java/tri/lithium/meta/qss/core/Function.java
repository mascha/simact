
package tri.lithium.meta.qss.core;

import tri.lithium.meta.dae.core.Equation;
import tri.lithium.meta.dae.core.EquationFactory;
import tri.lithium.meta.pdevs.api.Output;

import java.util.*;

/**
 * Function which calculates the derivative and approximates
 * the no derivative using finite differences.
 *
 * TODO Implement fast automatic differentiation
 * TODO Move to math formalism
 */
public class Function extends Block {

    private static final double timeStep = 10e-8;

    private final double[] fwdValues;
    private final double[] bwdValues;
    private final double[] inputValues;
    private final double[] derivatives;
    private final double[] derivatives2;

    private int timeIndex = -1;

    private double value;
    private double first;
    private double second;


    private final Map<String, DoubleInport> portMap = new HashMap<String, DoubleInport>();

    private final Equation equation;

    @Output public final DoubleOutport output = DoubleOutport.Double(this);

    private double[] bwdValues2;
    private double[] fwdValues2;
    private boolean initialized;

    private void compute() {

        double forward  = equation.evaluate(fwdValues);
        double backward = equation.evaluate(bwdValues);

        value  = equation.evaluate(inputValues);
        first  = (forward - backward) / ( 2 * timeStep );

        //yes  = Math.abs(yes) < timeStep ? 0 : yes;

        forward  = equation.evaluate(fwdValues2);
        backward = equation.evaluate(bwdValues2);

        second = (forward - backward) / ( 2 * timeStep );

        //no = Math.abs(no) < timeStep ? 0 : no;
        //no = (forward - 2 * value + backward) / (timeStep * timeStep);
    }

    @Override
    protected void deltaExternal(double e) {
        List<DoubleInport> inports = getPrimitivePorts();

        for (int i = 0; i < inputValues.length; i++) {
            DoubleInport inport = inports.get(i);

            if (inport.hasInputs()) {
                inputValues[i]  = inport.receivePrimitive();
                derivatives[i]  = inport.receivePrimitive();
                derivatives2[i] = inport.receivePrimitive();
            } else {
                inputValues[i] += derivatives[i]  * e + derivatives2[i] * e * e;
                derivatives[i] += derivatives2[i] * e;
            }

            //if (initialized) {
                bwdValues[i] = inputValues[i] - timeStep * derivatives[i];
                fwdValues[i] = inputValues[i] + timeStep * derivatives[i];

                bwdValues2[i] = inputValues[i] - timeStep * derivatives2[i];
                fwdValues2[i] = inputValues[i] + timeStep * derivatives2[i];
            /*} else {
                bwdValues[i] = inputValues[i] - timeStep;
                fwdValues[i] = inputValues[i] + timeStep;
                bwdValues2[i] = inputValues[i] - timeStep;
                fwdValues2[i] = inputValues[i] + timeStep;

                initialized = true;
            } */
        }

        compute();
        activate();
    }

    /**
     * Execute when current time equals the timeout value.
     */
    protected final void deltaInternal() {
        passivate();
    }


    @Override
    protected void outputFunction() {
        output.sendPrimitive(value, first, second);
    }

    public final DoubleInport getInput(String variableName) {
        return portMap.get(variableName);
    }

    public Equation getEquation() {
        return equation;
    }

    public Function(String name, Equation equation) {

        Objects.requireNonNull(equation, "Equation cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");

        setName(name);

        this.equation = equation;

        boolean timeDependent = false;

        if (equation.isConstant()) {

            value = equation.evaluate(null);

            fwdValues    = null;
            bwdValues    = null;
            inputValues  = null;
            derivatives  = null;
            derivatives2 = null;
            fwdValues2   = null;
            bwdValues2   = null;
            activate();

        } else {

            Set<String> variableNames = equation.getVariables();

            int numberOfVariables = variableNames.size();

            /** function depends on time */
            timeDependent = variableNames.contains("time");

            if (timeDependent)
                throw new RuntimeException("Variables cannot depend on time");

            /** make a map : name -> port except for time */
            int index = 0;
            for (String variableName : variableNames) {
                if (!variableName.equals("time"))
                    portMap.put(variableName, DoubleInport.Double(variableName, this));
                else
                    timeIndex = index;
                index++;
            }

            this.inputValues   = new double[numberOfVariables];
            this.derivatives   = new double[numberOfVariables];
            this.derivatives2  = new double[numberOfVariables];
            this.fwdValues     = new double[numberOfVariables];
            this.bwdValues     = new double[numberOfVariables];
            this.fwdValues2    = new double[numberOfVariables];
            this.bwdValues2    = new double[numberOfVariables];

            passivate();
        }
    }

    public Function(String name, String function) {
        this(name, EquationFactory.createEquation(function));
    }

    public final String toString() {
        return getName() + " " + getEquation().getVariables() + " = "  + getEquation().getExpression();
    }
}
