package com.cegesoft.prepa.rank.impl;

import com.cegesoft.prepa.server.Server;
import net.dv8tion.jda.api.entities.User;

public class BDE2024InfoGathering extends BDEInfoGathering {

    private static final long MPSI_ROLE = 1139232591019507782L;
    private static final long MP_ROLE = 1139233007476162610L;
    private static final long MP5_2_ROLE = 1139233413837099122L;
    private static final long INTEGRE_ROLE = 1139321403120832522L;

    public BDE2024InfoGathering(Server server, User user) {
        super(server, user, MPSI_ROLE, MP_ROLE, MP5_2_ROLE, INTEGRE_ROLE);
    }
}
