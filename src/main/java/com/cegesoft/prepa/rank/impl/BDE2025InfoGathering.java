package com.cegesoft.prepa.rank.impl;

import com.cegesoft.prepa.rank.InfoGathering;
import com.cegesoft.prepa.server.Server;
import net.dv8tion.jda.api.entities.User;

public class BDE2025InfoGathering  extends BDEInfoGathering {
    private static final long MPSI_ROLE = 1257328301056331898L;
    private static final long MP_ROLE = 1257327920829960293L;
    private static final long MP5_2_ROLE = 1257325923712237681L;
    private static final long INTEGRE_ROLE = 1257329544751222875L;

    public BDE2025InfoGathering(Server server, User user) {
        super(server, user, MPSI_ROLE, MP_ROLE, MP5_2_ROLE, INTEGRE_ROLE);
    }
}
