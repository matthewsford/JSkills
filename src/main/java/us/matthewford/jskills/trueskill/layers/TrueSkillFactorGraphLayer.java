package us.matthewford.jskills.trueskill.layers;

import us.matthewford.jskills.factorgraphs.Factor;
import us.matthewford.jskills.factorgraphs.FactorGraphLayer;
import us.matthewford.jskills.factorgraphs.Variable;
import us.matthewford.jskills.numerics.GaussianDistribution;
import us.matthewford.jskills.trueskill.TrueSkillFactorGraph;

public abstract class TrueSkillFactorGraphLayer<TInputVariable extends Variable<GaussianDistribution>, 
                                                TFactor extends Factor<GaussianDistribution>,
                                                TOutputVariable extends Variable<GaussianDistribution>>
    extends FactorGraphLayer
            <TrueSkillFactorGraph, GaussianDistribution, Variable<GaussianDistribution>, TInputVariable,
            TFactor, TOutputVariable> 
{
    public TrueSkillFactorGraphLayer(TrueSkillFactorGraph parentGraph)
    {
        super(parentGraph);
    }
}