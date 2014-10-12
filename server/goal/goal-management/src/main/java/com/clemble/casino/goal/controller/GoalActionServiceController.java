package com.clemble.casino.goal.controller;

import com.clemble.casino.WebMapping;
import com.clemble.casino.event.Event;
import com.clemble.casino.goal.GoalWebMapping;
import com.clemble.casino.goal.action.GoalManagerFactoryFacade;
import com.clemble.casino.goal.event.GoalEvent;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.lifecycle.management.service.GoalActionService;
import com.clemble.casino.goal.repository.GoalStateRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by mavarazy on 10/9/14.
 */
@RestController
public class GoalActionServiceController implements GoalActionService {

    final private GoalManagerFactoryFacade factoryFacade;
    final private GoalStateRepository stateRepository;

    public GoalActionServiceController(GoalManagerFactoryFacade factoryFacade, GoalStateRepository stateRepository) {
        this.factoryFacade = factoryFacade;
        this.stateRepository = stateRepository;
    }

    @Override
    public List<GoalState> myActive() {
        throw new IllegalAccessError();
    }

    @RequestMapping(method = RequestMethod.GET, value = GoalWebMapping.MY_ACTIVE_GOALS, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public List<GoalState> myActive(@CookieValue("player") String player) {
        return stateRepository.findByPlayer(player);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = GoalWebMapping.PLAYER_ACTIVE_GOALS, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public List<GoalState> getActive(@PathVariable("player") String player) {
        return stateRepository.findByPlayer(player);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = GoalWebMapping.GOAL_ACTIONS, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public GoalEvent process(String goalKey, Event action) {
        return factoryFacade.get(goalKey).process(action);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = GoalWebMapping.GOAL_STATE, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public GoalState getState(String goalKey) {
        return factoryFacade.get(goalKey).getState();
    }

}