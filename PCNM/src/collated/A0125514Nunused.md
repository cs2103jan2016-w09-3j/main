# A0125514Nunused
###### userinterface\DescriptionComponent.java
``` java
    // to facilitate changing of ui themen, all css is changed to be controlled
    // with stylesheets
    public Rectangle buildGradientRec(double width, double height, boolean isSelected) {
        Stop[] stops;
        if (isSelected) {
            stops = new Stop[] { new Stop(0, new Color(1, 0.7, 0.5, 0)),
                    new Stop(0.1, new Color(1, 0.7, 0.5, 0.90)), new Stop(1, new Color(1, 0.7, 0.5, 0.95)) };
        } else {
            stops = new Stop[] { new Stop(0, new Color(1, 1, 1, 0)), new Stop(0.1, new Color(1, 1, 1, 0.9)),
                    new Stop(1, new Color(1, 1, 1, 0.9)) };
        }
        LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        Rectangle rec = new Rectangle(0, 0, width, height);
        rec.setFill(lg1);
        return rec;
    }

```
