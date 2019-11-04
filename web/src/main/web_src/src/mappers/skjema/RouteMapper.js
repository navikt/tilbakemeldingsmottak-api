const classifyRoutes = (question, route) => {
  const routedQuestionAnswers = (question.answers || []).map(
    (answer, answerIndex) => {
      const answerObject = typeof answer === "string" ? { answer } : answer;
      const answerQuestions = (answer.questions || []).map(nestedQuestion =>
        classifyRoutes(nestedQuestion, [...route, answerIndex])
      );
      return {
        ...answerObject,
        ...(answerQuestions.length > 0 ? { questions: answerQuestions } : {})
      };
    }
  );
  return {
    ...question,
    ...(routedQuestionAnswers.length > 0
      ? { answers: routedQuestionAnswers }
      : {}),
    route
  };
};

const flattenNestedRoutes = questionArray => {
  const flattenRoutes = question => {
    const flattenedAnswers = (question.answers || []).map(answer =>
      (answer.questions || []).map(flattenRoutes).flat()
    );
    return [question, ...flattenedAnswers.flat()];
  };
  return questionArray.map(flattenRoutes).flat();
};

export default questions => {
  const routes = questions.map((question, index) =>
    classifyRoutes(question, [index])
  );
  return flattenNestedRoutes(routes);
};
