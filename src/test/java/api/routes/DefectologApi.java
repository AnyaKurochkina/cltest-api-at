package api.routes;

import core.helper.http.Path;

import static core.helper.Configure.KONG_URL;

public class DefectologApi implements Api {
    @Route(method = Method.GET, path = "/defect-pages/", status = 200)
    public static Path defectPagesList;

    @Route(method = Method.GET, path = "/defect-pages/{id}/", status = 200)
    public static Path defectPagesRead;

    @Route(method = Method.GET, path = "/defects/", status = 200)
    public static Path defectsList;

    @Route(method = Method.GET, path = "/defects/{id}/", status = 200)
    public static Path defectsRead;

    @Route(method = Method.GET, path = "/health/", status = 200)
    public static Path healthList;

    @Route(method = Method.GET, path = "/tasks/", status = 200)
    public static Path tasksList;

    @Route(method = Method.POST, path = "/tasks/", status = 201)
    public static Path tasksCreate;

    @Override
    public String url() {
        return KONG_URL + "tags-defectolog/api/v1";
    }
}
