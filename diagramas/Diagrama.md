```mermaid
classDiagram
    class ConfigManager {
        - String configPath
        - Properties properties
        + loadConfig() void
        + getProperty(String key) String
    }

    class Extractor {
        <<interface>>
        - String sourcePath
        + extractData() DataFrame
    }

    class CSVExtractor {
        - String csvFilePath
        + extractData() DataFrame
    }

    class DatabaseExtractor {
        - String connectionString
        + extractData() DataFrame
    }

    class Transformer {
        - List cleaningRules
        + cleanData(DataFrame df) DataFrame
        + transformData(DataFrame df) DataFrame
    }

    class Loader {
        - String targetConnection
        + loadData(DataFrame df) void
    }

    class ETLPipeline {
        - Extractor extractor
        - Transformer transformer
        - Loader loader
        + runPipeline() void
    }

    CSVExtractor --|> Extractor : implements
    DatabaseExtractor --|> Extractor : implements
    ETLPipeline --> Extractor : uses
    ETLPipeline --> Transformer : uses
    ETLPipeline --> Loader : uses
