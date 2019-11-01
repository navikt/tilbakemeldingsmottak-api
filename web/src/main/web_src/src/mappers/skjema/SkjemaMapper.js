import RouteMapper from "./RouteMapper";
import getNextRoute from "./NextRoute";

const filterObjectKeys = (object, keys) => {
  return keys.reduce((acc, key) => {
    return {
      ...acc,
      ...(object[key] ? { [key]: object[key] } : {})
    };
  }, {});
};

const filterQuestion = question => {
  return filterObjectKeys(question, ["text", "id"]);
};

const filterQuestionRenderProperties = question => {
  return {
    ...filterObjectKeys(question, ["text", "type", "index", "id"]),
    properties: question.properties || {}
  };
};

const filterEmitProperties = object => {
  return filterObjectKeys(object, ["id", "emit"]);
};

const indexAnswers = args => {
  const { question } = args;
  return question.answers.map((answer, answerIndex) => {
    return {
      answer: answer.answer,
      emitValues: {
        ...filterEmitProperties(answer),
        id: question.id,
        question: filterQuestion(question),
        next: getNextRoute({
          ...args,
          route: question.route,
          next: answer.next,
          answerIndex
        })
      }
    };
  });
};

const indexRoutes = args => {
  const { questions, questionIndex = 0 } = args;
  return questions.map((question, index) => {
    return {
      ...filterQuestionRenderProperties(question),
      ...(question.answers
        ? {
            answers: indexAnswers({
              ...args,
              question,
              questionIndex: index + questionIndex
            })
          }
        : {}),
      emitValues: {
        question: filterQuestion(question),
        next: getNextRoute({
          ...args,
          route: question.route,
          next: question.next,
          questionIndex: index + questionIndex
        }),
        ...filterEmitProperties(question)
      }
    };
  });
};

const normializeValues = questions =>
  questions.map(question => {
    return {
      ...question,
      ...(typeof question.emit === "string"
        ? { emit: { message: question.emit, type: "info" } }
        : {}),
      ...(question.answers
        ? {
            answers: question.answers.map(answer => ({
              ...answer,
              ...(typeof answer.emit === "string"
                ? { emit: { message: answer.emit, type: "info" } }
                : {})
            }))
          }
        : {}),
      type: question.type.toLowerCase()
    };
  });

export const SchemaMapper = schemaQuestions => {
  const questions = RouteMapper(schemaQuestions || []);
  const sanizied = JSON.parse(
    JSON.stringify(questions, (key, val) => (val === null ? undefined : val))
  );
  const routeArray = sanizied.map(question => question.route);
  const routeIds = sanizied.map(question => question.id);

  return indexRoutes({
    questions: normializeValues(sanizied),
    routeArray,
    routeIds
  });
};

export const DefaultAnswersMapper = defaultAnswers => {
  const { answers } = defaultAnswers || { answers: {} };
  return {
    ...defaultAnswers,
    answers: Object.entries(answers).reduce(
      (acc, [key, val]) => ({
        ...acc,
        [key]: typeof val === "string" ? { answer: val } : val
      }),
      {}
    )
  };
};
