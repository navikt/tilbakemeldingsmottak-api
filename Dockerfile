FROM gcr.io/distroless/java25-debian13:nonroot

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

COPY app/target/app.jar /app/app.jar

WORKDIR /app

CMD ["app.jar"]
