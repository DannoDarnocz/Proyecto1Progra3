package resourcemanager.ui;

public class StatsTabController {
/*
    // cantidad de recursos usados por categoría en un periodo
    @FXML
    private DatePicker dt_resource_start;   // fecha inicio del periodo
    @FXML
    private DatePicker dt_resource_end;   // fecha fin del periodo
    @FXML
    private Button btn_resource_search;
    @FXML
    private BarChart<String, Number> graph_resources;

    // # de reservas por semana en un rango de semanas
    @FXML
    private DatePicker dt_week_start;
    @FXML
    private DatePicker dt_week_end;
    @FXML
    private Button btn_week_search;
    @FXML
    private BarChart<String, Number> graph_activities;

    private static final DateTimeFormatter LABEL_FORMAT = DateTimeFormatter.ofPattern("dd/MM");

    @FXML
    private void initialize() {
        btn_resource_search.setOnAction(e -> onSearchResourcesByCategory());
        btn_week_search.setOnAction(e -> onSearchReservationsByWeek());

    }


    private void onSearchResourcesByCategory() {
        LocalDate start = dt_resource_start.getValue();
        LocalDate end = dt_resource_end.getValue();

        if (start == null || end == null) {
            showError("Debe seleccionar la fecha de inicio y la fecha final del periodo.");
            return;
        }
        if (start.isAfter(end)) {
            showError("La fecha de inicio no puede ser posterior a la fecha final.");
            return;
        }

        // por cada categoría, contar recursos diferentes usados dentro de ella en el periodo
        Map<String, Set<String>> usedResourcesByCategory = new HashMap<>();

        for (Reservation reservation : GlobalLists.reservationList) {
            if (!overlaps(reservation, start, end)) {
                continue;
            }
            Resource resource = reservation.getResource();
            if (resource == null || resource.getCategory() == null) {
                continue;
            }
            String categoryId = resource.getCategory().getId();
            usedResourcesByCategory
                    .computeIfAbsent(categoryId, k -> new HashSet<>())
                    .add(resource.getId());
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Recursos usados");

        for (Category category : GlobalLists.categoryList) {
            Set<String> usedResources = usedResourcesByCategory.get(category.getId());
            int count = (usedResources == null) ? 0 : usedResources.size();
            series.getData().add(new XYChart.Data<>(category.getDescription(), count));
        }

        graph_resources.getData().clear();
        graph_resources.getData().add(series);
    }

    // true si el rango de la reserva se solapa con [start, end]
    private boolean overlaps(Reservation reservation, LocalDate start, LocalDate end) {
        LocalDate resStart = reservation.getStartDate();
        LocalDate resEnd = reservation.getEndDate();
        if (resStart == null || resEnd == null) {
            return false;
        }
        return !resStart.isAfter(end) && !resEnd.isBefore(start);
    }

    private void onSearchReservationsByWeek() {
        LocalDate start = dt_week_start.getValue();
        LocalDate end = dt_week_end.getValue();

        if (start == null || end == null) {
            showError("Debe seleccionar la fecha de inicio y la fecha final del rango de semanas.");
            return;
        }
        if (start.isAfter(end)) {
            showError("La fecha de inicio no puede ser posterior a la fecha final.");
            return;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Reservas por semana");

        int weekNumber = 1;
        LocalDate weekStart = start;

        while (!weekStart.isAfter(end)) {
            LocalDate weekEnd = weekStart.plusDays(6);
            if (weekEnd.isAfter(end)) {
                weekEnd = end;
            }

            long count = countReservationsStartingBetween(weekStart, weekEnd);

            String label = "Sem " + weekNumber + "\n" + weekStart.format(LABEL_FORMAT)
                    + "-" + weekEnd.format(LABEL_FORMAT);
            series.getData().add(new XYChart.Data<>(label, count));

            weekStart = weekStart.plusDays(7);
            weekNumber++;
        }

        graph_activities.getData().clear();
        graph_activities.getData().add(series);
    }

    // cuenta las reservas cuya fecha de inicio cae dentro de [from, to]
    private long countReservationsStartingBetween(LocalDate from, LocalDate to) {
        long count = 0;
        for (Reservation reservation : GlobalLists.reservationList) {
            LocalDate resStart = reservation.getStartDate();
            if (resStart == null) {
                continue;
            }
            if (!resStart.isBefore(from) && !resStart.isAfter(to)) {
                count++;
            }
        }
        return count;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }*/
}
