package MobilityViewer.project.utils;

public abstract class TickTranslator {
    public final long convert(long tick){
        return tick % duration();
    }

    public abstract long duration();

    public static class BaseTickTranslator extends TickTranslator {
        @Override
        public long duration(){ return 365L * 24L * 60L * 60L * 1000000L; }
    }

    public static class MonthTickTranslator extends TickTranslator {
        @Override
        public long duration() { return (31L * 24L * 60L * 60L * 1000000L); }
    }

    public static class WeekTickTranslator extends TickTranslator {
        @Override
        public long duration() { return (7L * 24L * 60L * 60L * 1000000L); }
    }

    public static class DayTickTranslator extends TickTranslator {
        @Override
        public long duration() { return (24L * 60L * 60L * 1000000L); }
    }
}
