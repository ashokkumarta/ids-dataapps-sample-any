call mvnw clean package
call docker container prune
call docker image rm ashokkumarta/dataapps-sample:latest
call docker build -t ashokkumarta/dataapps-sample:latest .
call docker push ashokkumarta/dataapps-sample:latest

call cd ..\ids-test-dataapps\test-provider
call docker image rm ashokkumarta/test_provider:latest
call docker build -t ashokkumarta/test_provider:latest .
call docker push ashokkumarta/test_provider:latest

call cd ..\test-consumer
call docker image rm ashokkumarta/test_consumer:latest
call docker build -t ashokkumarta/test_consumer:latest .
call docker push ashokkumarta/test_consumer:latest

call cd ..\test-processor
call docker image rm ashokkumarta/test_processor:latest
call docker build -t ashokkumarta/test_processor:latest .
call docker push ashokkumarta/test_processor:latest

call cd ..\test-generic
call docker image rm ashokkumarta/test_generic:latest
call docker build -t ashokkumarta/test_generic:latest .
call docker push ashokkumarta/test_generic:latest

call cd ..\test-auditor
call docker image rm ashokkumarta/test_auditor:latest
call docker build -t ashokkumarta/test_auditor:latest .
call docker push ashokkumarta/test_auditor:latest

call cd ..\test-encryptor
call docker image rm ashokkumarta/test_encryptor:ltest
call docker build -t ashokkumarta/test_encryptor:latest .
call docker push ashokkumarta/test_encryptor:latest

call cd ..\test-validator
call docker image rm ashokkumarta/test_validator:latest
call docker build -t ashokkumarta/test_validator:latest .
call docker push ashokkumarta/test_validator:latest
