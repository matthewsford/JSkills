package us.matthewford.jskills.trueskill.layers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import us.matthewford.jskills.IPlayer;
import us.matthewford.jskills.ITeam;
import us.matthewford.jskills.Rating;
import us.matthewford.jskills.factorgraphs.DefaultVariable;
import us.matthewford.jskills.factorgraphs.KeyedVariable;
import us.matthewford.jskills.factorgraphs.Schedule;
import us.matthewford.jskills.factorgraphs.ScheduleStep;
import us.matthewford.jskills.factorgraphs.Variable;
import us.matthewford.jskills.numerics.GaussianDistribution;
import us.matthewford.jskills.numerics.MathUtils;
import us.matthewford.jskills.trueskill.TrueSkillFactorGraph;
import us.matthewford.jskills.trueskill.factors.GaussianPriorFactor;

// We intentionally have no Posterior schedule since the only purpose here is to 
public class PlayerPriorValuesToSkillsLayer extends
    TrueSkillFactorGraphLayer<DefaultVariable<GaussianDistribution>, 
                              GaussianPriorFactor,
                              KeyedVariable<IPlayer, GaussianDistribution>>
{
    private final Collection<ITeam> _Teams;

    public PlayerPriorValuesToSkillsLayer(TrueSkillFactorGraph parentGraph,
                                          Collection<ITeam> teams)
    {
        super(parentGraph);
        _Teams = teams;
    }

    @Override
    public void BuildLayer()
    {
        for(ITeam currentTeam : _Teams)
        {
            List<KeyedVariable<IPlayer, GaussianDistribution>> currentTeamSkills = new ArrayList<KeyedVariable<IPlayer, GaussianDistribution>>();

            for(Entry<IPlayer, Rating> currentTeamPlayer : currentTeam.entrySet())
            {
                KeyedVariable<IPlayer, GaussianDistribution> playerSkill =
                    CreateSkillOutputVariable(currentTeamPlayer.getKey());
                AddLayerFactor(CreatePriorFactor(currentTeamPlayer.getKey(), currentTeamPlayer.getValue(), playerSkill));
                currentTeamSkills.add(playerSkill);
            }

            addOutputVariableGroup(currentTeamSkills);
        }
    }

    @Override
    public Schedule<GaussianDistribution> createPriorSchedule()
    {
        Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<Schedule<GaussianDistribution>>();
        for (GaussianPriorFactor prior : getLocalFactors()) {
            schedules.add(new ScheduleStep<GaussianDistribution>("Prior to Skill Step", prior, 0));
        }
        return ScheduleSequence(schedules, "All priors");
    }

    private GaussianPriorFactor CreatePriorFactor(IPlayer player, Rating priorRating,
                                                  Variable<GaussianDistribution> skillsVariable)
    {
        return new GaussianPriorFactor(priorRating.getMean(),
                                       MathUtils.square(priorRating.getStandardDeviation()) +
                                       MathUtils.square(getParentFactorGraph().getGameInfo().getDynamicsFactor()), skillsVariable);
    }

    private KeyedVariable<IPlayer, GaussianDistribution> CreateSkillOutputVariable(IPlayer key)
    {
        return new KeyedVariable<IPlayer, GaussianDistribution>(key, GaussianDistribution.UNIFORM, "%s's skill", key.toString());
    }
}