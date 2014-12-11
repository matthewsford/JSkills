package us.matthewford.jskills.trueskill.layers;

import us.matthewford.jskills.GameInfo;
import us.matthewford.jskills.factorgraphs.DefaultVariable;
import us.matthewford.jskills.factorgraphs.Variable;
import us.matthewford.jskills.numerics.GaussianDistribution;
import us.matthewford.jskills.trueskill.DrawMargin;
import us.matthewford.jskills.trueskill.TrueSkillFactorGraph;
import us.matthewford.jskills.trueskill.factors.GaussianFactor;
import us.matthewford.jskills.trueskill.factors.GaussianGreaterThanFactor;
import us.matthewford.jskills.trueskill.factors.GaussianWithinFactor;

public class TeamDifferencesComparisonLayer extends
    TrueSkillFactorGraphLayer<Variable<GaussianDistribution>, GaussianFactor, DefaultVariable<GaussianDistribution>>
{
    private final double _Epsilon;
    private final int[] _TeamRanks;

    public TeamDifferencesComparisonLayer(TrueSkillFactorGraph parentGraph, int[] teamRanks)
    {
        super(parentGraph);
        _TeamRanks = teamRanks;
        GameInfo gameInfo = ParentFactorGraph.getGameInfo();
        _Epsilon = DrawMargin.GetDrawMarginFromDrawProbability(gameInfo.getDrawProbability(), gameInfo.getBeta());
    }

    @Override
    public void BuildLayer()
    {
        for (int i = 0; i < getInputVariablesGroups().size(); i++)
        {
            boolean isDraw = (_TeamRanks[i] == _TeamRanks[i + 1]);
            Variable<GaussianDistribution> teamDifference = getInputVariablesGroups().get(i).get(0);

            GaussianFactor factor =
                isDraw
                    ? (GaussianFactor) new GaussianWithinFactor(_Epsilon, teamDifference)
                    : new GaussianGreaterThanFactor(_Epsilon, teamDifference);

            AddLayerFactor(factor);
        }
    }
}