/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.cli.parsing.operation;

import org.jboss.as.cli.CommandFormatException;
import org.jboss.as.cli.parsing.BackQuotesState;
import org.jboss.as.cli.parsing.CharacterHandler;
import org.jboss.as.cli.parsing.DefaultStateWithEndCharacter;
import org.jboss.as.cli.parsing.ExpressionBaseState;
import org.jboss.as.cli.parsing.GlobalCharacterHandlers;
import org.jboss.as.cli.parsing.ParsingContext;
import org.jboss.as.cli.parsing.QuotesState;
import org.jboss.as.cli.parsing.WordCharacterHandler;

/**
 *
 * @author Alexey Loubyansky
 */
public class PropertyValueState extends ExpressionBaseState {

    public static final PropertyValueState INSTANCE = new PropertyValueState();
    public static final String ID = "PROP_VALUE";

    PropertyValueState() {
        this(',', ')');
    }

    PropertyValueState(char propSeparator, char... listEnd) {
        super(ID, false);
        this.setEnterHandler(new CharacterHandler() {
            @Override
            public void handle(ParsingContext ctx) throws CommandFormatException {
                getHandler(ctx.getCharacter()).handle(ctx);
            }});
        putHandler(propSeparator, GlobalCharacterHandlers.LEAVE_STATE_HANDLER);
        for(int i = 0; i < listEnd.length; ++i) {
            putHandler(listEnd[i], GlobalCharacterHandlers.LEAVE_STATE_HANDLER);
        }
        enterState('"', QuotesState.QUOTES_INCLUDED_KEEP_ESCAPES);
        enterState('`', BackQuotesState.QUOTES_INCLUDED_KEEP_ESCAPES);
        enterState('[', DefaultStateWithEndCharacter.builder("BRACKETS")
                .setLeaveStateChar(']')
                .setEndRequired(true)
                .setEnterLeaveContent(true)
                .setEnterStateHandlers(enterStateHandlers)
                .setResolveSystemProperties(false)
                .build());
        enterState('(', DefaultStateWithEndCharacter.builder("PARENTHESIS")
                .setLeaveStateChar(')')
                .setEndRequired(true)
                .setEnterLeaveContent(true)
                .setEnterStateHandlers(enterStateHandlers)
                .setResolveSystemProperties(false)
                .build());
        enterState('{', DefaultStateWithEndCharacter.builder("BRACES")
                .setLeaveStateChar('}')
                .setEndRequired(true)
                .setEnterLeaveContent(true)
                .setEnterStateHandlers(enterStateHandlers)
                .setResolveSystemProperties(false)
                .build());
        setDefaultHandler(WordCharacterHandler.IGNORE_LB_ESCAPE_OFF);
    }

    @Override
    public boolean lockValueIndex() {
        return true;
    }
}
