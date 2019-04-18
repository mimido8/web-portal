package com.healthware.routes;

import com.healthware.Session;
import com.healthware.base.http.HTTPRequest;
import com.healthware.base.http.HTTPResponse;
import com.healthware.base.http.JSONRoute;
import com.healthware.base.sql.Table;
import com.healthware.models.Plan;

import java.util.Map;

import static com.healthware.base.http.HTTPResponse.object;

public class PlansRoute extends JSONRoute {
    private Table<Plan> plans;
    private Map<String, Session> sessions;

    public PlansRoute(Table<Plan> plans, Map<String, Session> sessions) {
        this.plans = plans;
        this.sessions = sessions;
    }

    @Override
    protected HTTPResponse getObject(HTTPRequest request) throws Exception {
        return object(plans.selectAll());
    }
}
