/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crcl.langaugeprocessing.tagger;

/**
 *
 * @author MS Vaswani
 */
public class Viterbi {

    private static String[] states;
    private static String[] observations;
    private static double[] start_probability;
    private static double[][] transition_probability;
    private static double[][] emission_probability;

    static int[] start() {
        return forwardViterbi(observations, states, start_probability, transition_probability, emission_probability);
    }

    public Viterbi(String[] observations, String[] states, double[] start_probability, double[][] transition_probability, double[][] emission_probability) {
        this.states = states;
        this.observations = observations;
        this.start_probability = start_probability;
        this.transition_probability = transition_probability;
        this.emission_probability = emission_probability;
    }

    private static class TNode {

        public double prob;
        public int[] v_path;
        public double v_prob;

        public TNode(double prob, int[] v_path, double v_prob) {
            this.prob = prob;
            this.v_path = copyIntArray(v_path);
            this.v_prob = v_prob;
        }
    }

    private static int[] copyIntArray(int[] ia) {
        int[] newIa = new int[ia.length];
        for (int i = 0; i < ia.length; i++) {
            newIa[i] = ia[i];
        }
        return newIa;
    }

    private static int[] copyIntArray(int[] ia, int newInt) {
        int[] newIa = new int[ia.length + 1];
        for (int i = 0; i < ia.length; i++) {
            newIa[i] = ia[i];
        }
        newIa[ia.length] = newInt;
        return newIa;
    }

    public static int[] forwardViterbi(String[] y, String[] X, double[] sp, double[][] tp, double[][] ep) {
        TNode[] T = new TNode[X.length];
        for (int state = 0; state < X.length; state++) {
            int[] intArray = new int[1];
            intArray[0] = state;
            T[state] = new TNode(sp[state], intArray, sp[state]);
        }

        for (int output = 0; output < y.length; output++) {
            TNode[] U = new TNode[X.length];
            for (int next_state = 0; next_state < X.length; next_state++) {
                double total = 0;
                int[] argmax = new int[0];
                double valmax = 0;
                for (int state = 0; state < X.length; state++) {
                    double prob = T[state].prob;
                    int[] v_path = copyIntArray(T[state].v_path);
                    double v_prob = T[state].v_prob;
                    double p = ep[state][output] * tp[state][next_state];
                    prob *= p;
                    v_prob *= p;
                    total += prob;
                    if (v_prob > valmax) {
                        argmax = copyIntArray(v_path, next_state);
                        valmax = v_prob;
                    }
                }
                U[next_state] = new TNode(total, argmax, valmax);
            }
            T = U;
        }
        // apply sum/max to the final states:
        double total = 0;
        int[] argmax = new int[0];
        double valmax = 0;
        for (int state = 0; state < X.length; state++) {
            double prob = T[state].prob;
            int[] v_path = copyIntArray(T[state].v_path);
            double v_prob = T[state].v_prob;
            total += prob;
            if (v_prob > valmax) {
                argmax = copyIntArray(v_path);
                valmax = v_prob;
            }
        }

       
        return argmax;
    }

}
