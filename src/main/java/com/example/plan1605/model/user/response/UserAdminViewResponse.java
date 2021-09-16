package com.example.plan1605.model.user.response;

import com.example.plan1605.model.user.PlannerUser;

public record UserAdminViewResponse(UserResponse userResponse,
                                    int numberOfEvents,
                                    int numberOfNotifyingEvents,
                                    int numberOfRecurringEvents
) {
    public static UserAdminViewResponse fromUser(PlannerUser plannerUser,
                                                 int numberOfEvents,
                                                 int numberOfNotifyingEvents,
                                                 int numberOfRecurringEvents) {
        return new UserAdminViewResponse(UserResponse.fromUser(plannerUser),
                                         numberOfEvents,
                                         numberOfNotifyingEvents,
                                         numberOfRecurringEvents);
    }
}
