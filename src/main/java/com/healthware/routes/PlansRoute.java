package com.healthware.routes;

import com.healthware.base.http.HTTPRequest;
import com.healthware.base.http.HTTPResponse;
import com.healthware.base.http.JSONRoute;
import com.healthware.base.sql.Table;
import com.healthware.models.Plan;

import static com.healthware.base.http.HTTPResponse.object;

public class PlansRoute extends JSONRoute {
    private Table<Plan> plans;

    public PlansRoute(Table<Plan> plans) {
        this.plans = plans;
    }

    @Override
    protected HTTPResponse getObject(HTTPRequest request) throws Exception {
        return object(plans.selectAll());
    }
}
